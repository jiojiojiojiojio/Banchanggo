package com.penelope.banchanggo.ui.mypage.mypage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.data.notice.Notice;
import com.penelope.banchanggo.databinding.DialogNoticeBinding;
import com.penelope.banchanggo.databinding.FragmentMyPageBinding;
import com.penelope.banchanggo.utils.TimeUtils;
import com.penelope.banchanggo.utils.ui.AuthFragment;
import com.penelope.banchanggo.utils.BitmapUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyPageFragment extends AuthFragment {

    private FragmentMyPageBinding binding;
    private MyPageViewModel viewModel;

    private ActivityResultLauncher<Intent> imageActivityLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;


    public MyPageFragment() {
        super(R.layout.fragment_my_page);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        // 비트맵 획득
                        Bitmap bitmap = null;
                        if (result.getData().getExtras() != null) {
                            // 카메라 결과 획득
                            bitmap = (Bitmap) result.getData().getExtras().get("data");
                        } else {
                            // 갤러리(포토) 결과 획득
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                bitmap = BitmapUtils.getBitmapFromUri(requireContext(), uri);
                            }
                        }
                        viewModel.onImageSelected(bitmap);
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean permissionExternalStorage = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                    Boolean permissionCamera = result.get(Manifest.permission.CAMERA);

                    if (permissionExternalStorage != null && permissionExternalStorage
                            && permissionCamera != null && permissionCamera) {
                        showImageDialog();
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentMyPageBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(MyPageViewModel.class);

        binding.imageViewEditProfile.setOnClickListener(v -> viewModel.onEditProfileClick());
        binding.buttonSignOut.setOnClickListener(v -> viewModel.onSignOutClick());
        binding.buttonUnregister.setOnClickListener(v -> viewModel.onUnregisterClick());
        binding.buttonWriteNotice.setOnClickListener(v -> viewModel.onWriteNoticeClick());

        NoticesAdapter adapter = new NoticesAdapter();
        binding.recyclerNotice.setAdapter(adapter);
        binding.recyclerNotice.setHasFixedSize(true);
        adapter.setOnItemSelectedListener(position -> {
            Notice notice = adapter.getCurrentList().get(position);
            viewModel.onNoticeClick(notice);
        });

        viewModel.getBitmap().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                binding.imageViewProfile.setImageBitmap(bitmap);
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                String strGreeting = user.getName() + "님,";
                binding.textViewId.setText(strGreeting);
            }
        });

        viewModel.getNotices().observe(getViewLifecycleOwner(), notices -> {
            if (notices != null) {
                adapter.submitList(notices);
                binding.textViewNoNotice.setVisibility(notices.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.isAdmin().observe(getViewLifecycleOwner(), isAdmin ->
                binding.buttonWriteNotice.setVisibility(isAdmin ? View.VISIBLE : View.GONE));

        viewModel.isSelling().observe(getViewLifecycleOwner(), isSelling ->
                binding.textViewIsSelling.setText(isSelling ? "거래중" : "0")
        );

        viewModel.getCountSold().observe(getViewLifecycleOwner(), countSold ->
                binding.textViewCountSold.setText(String.valueOf(countSold))
        );

        viewModel.getCountBought().observe(getViewLifecycleOwner(), countBought ->
                binding.textViewCountBought.setText(String.valueOf(countBought))
        );

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof MyPageViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof MyPageViewModel.Event.PromptImage) {
                promptImage();
            } else if (event instanceof MyPageViewModel.Event.ShowGeneralMessage) {
                String message = ((MyPageViewModel.Event.ShowGeneralMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof MyPageViewModel.Event.ConfirmSignOut) {
                String message = ((MyPageViewModel.Event.ConfirmSignOut)event).message;
                showConfirmSignOutDialog(message);
            } else if (event instanceof MyPageViewModel.Event.ConfirmUnregister) {
                String message = ((MyPageViewModel.Event.ConfirmUnregister)event).message;
                showConfirmUnregisterDialog(message);
            } else if (event instanceof MyPageViewModel.Event.NavigateToAddNoticeScreen) {
                NavDirections action = MyPageFragmentDirections.actionMyPageFragmentToAddNoticeFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof MyPageViewModel.Event.ShowNoticeScreen) {
                Notice notice = ((MyPageViewModel.Event.ShowNoticeScreen) event).notice;
                showNoticeDialog(notice);
            }
        });

        getParentFragmentManager().setFragmentResultListener("add_notice_fragment", getViewLifecycleOwner(),
                (requestKey, result) -> viewModel.onAddNoticeResult(result.getBoolean("result"))
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        viewModel.onAuthStateChanged(firebaseAuth);
    }

    private void promptImage() {

        if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showImageDialog();
        } else {
            requestPermissionLauncher.launch(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}
            );
        }
    }

    private void showImageDialog() {

        // 업로드 방법 선택 대화상자 보이기
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(galleryIntent, "사진 업로드");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        imageActivityLauncher.launch(chooser);
    }

    private void showConfirmSignOutDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("로그아웃")
                .setMessage(message)
                .setPositiveButton("네", (dialog, which) -> viewModel.onSignOutConfirmed())
                .setNegativeButton("아니오", null)
                .show();
    }

    private void showConfirmUnregisterDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("회원탈퇴")
                .setMessage(message)
                .setPositiveButton("네", (dialog, which) -> viewModel.onUnregisterConfirmed())
                .setNegativeButton("아니오", null)
                .show();
    }

    private void showNoticeDialog(Notice notice) {

        DialogNoticeBinding dialogBinding = DialogNoticeBinding.inflate(getLayoutInflater());

        dialogBinding.textViewNoticeTitle.setText(notice.getTitle());
        dialogBinding.textViewNoticeContent.setText(notice.getContent());
        dialogBinding.textViewNoticeDate.setText(TimeUtils.getDateString(notice.getCreated()));

        new AlertDialog.Builder(context)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("확인", null)
                .show();
    }

}




