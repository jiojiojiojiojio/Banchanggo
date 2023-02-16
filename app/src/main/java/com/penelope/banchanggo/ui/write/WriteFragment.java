package com.penelope.banchanggo.ui.write;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.databinding.FragmentWriteBinding;
import com.penelope.banchanggo.utils.BitmapUtils;
import com.penelope.banchanggo.utils.OnTextChangedListener;
import com.penelope.banchanggo.utils.ui.AuthFragment;
import com.penelope.banchanggo.utils.ui.CardsAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WriteFragment extends AuthFragment {

    private FragmentWriteBinding binding;
    private WriteViewModel viewModel;

    private ActivityResultLauncher<Intent> imageActivityLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;


    public WriteFragment() {
        super(R.layout.fragment_write);
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
                                String path = BitmapUtils.getRealPathFromUri(requireContext(), uri);
                                bitmap = BitmapUtils.getBitmapFromPath(path);
                            }
                        }
                        viewModel.onImageSubmit(bitmap);
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

        binding = FragmentWriteBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(WriteViewModel.class);

        binding.imageViewPost.setOnClickListener(v -> viewModel.onImageClick());
        binding.editTextPostTitle.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onTitleChange(text);
            }
        });
        binding.editTextPostPrice.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onPriceChange(text);
            }
        });
        binding.textViewNegotiablePrice.setOnClickListener(v -> viewModel.onNegotiablePriceClick());
        binding.imageButtonAddCategory.setOnClickListener(v ->
                viewModel.onAddCategoryClick(binding.editTextCategory.getText().toString())
        );

        binding.editTextPostContent.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onContentChange(text);
            }
        });

        binding.buttonSetAddress.setOnClickListener(v -> viewModel.onSetAddressClick());
        binding.buttonSubmitPost.setOnClickListener(v -> viewModel.onSubmitPost());

        CardsAdapter adapter = new CardsAdapter();
        binding.recyclerCategory.setAdapter(adapter);
        binding.recyclerCategory.setHasFixedSize(true);

        viewModel.isNegotiablePrice().observe(getViewLifecycleOwner(), isNegotiable -> {
            binding.editTextPostPrice.setEnabled(!isNegotiable);
            if (isNegotiable) {
                binding.editTextPostPrice.setText("");
            }
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), adapter::submitList);

        viewModel.getImage().observe(getViewLifecycleOwner(), image -> {
            if (image != null) {
                binding.imageViewPost.setImageBitmap(image);
            }
        });

        viewModel.getAddress().observe(getViewLifecycleOwner(), binding.textViewAddress::setText);

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof WriteViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof WriteViewModel.Event.PromptImage) {
                promptImage();
            } else if (event instanceof WriteViewModel.Event.ShowGeneralMessage) {
                String message = ((WriteViewModel.Event.ShowGeneralMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof WriteViewModel.Event.NavigateToAddressScreen) {
                NavDirections action = WriteFragmentDirections.actionGlobalAddressFragment();
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        getParentFragmentManager().setFragmentResultListener("address_fragment", getViewLifecycleOwner(),
                (requestKey, result) -> {
                    String address = result.getString("address");
                    viewModel.onAddressSearched(address);
                });
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

}