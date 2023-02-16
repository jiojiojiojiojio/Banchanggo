package com.penelope.banchanggo.ui.address;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.penelope.banchanggo.R;
import com.penelope.banchanggo.databinding.FragmentAddressBinding;
import com.penelope.banchanggo.utils.OnTextChangedListener;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressFragment extends Fragment {

    private FragmentAddressBinding binding;
    private AddressViewModel viewModel;


    public AddressFragment() {
        super(R.layout.fragment_address);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentAddressBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        binding.editTextAddress.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onQueryAddressChange(text);
            }
        });
        binding.editTextAddressDetail.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onDetailAddressChange(text);
            }
        });

        binding.buttonSearchAddress.setOnClickListener(v -> viewModel.onSearchAddressClick());

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof AddressViewModel.Event.ShowGeneralMessage) {
                String message = ((AddressViewModel.Event.ShowGeneralMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof AddressViewModel.Event.NavigateBackWithResult) {
                String address = ((AddressViewModel.Event.NavigateBackWithResult) event).address;
                Bundle result = new Bundle();
                result.putString("address", address);
                getParentFragmentManager().setFragmentResult("address_fragment", result);
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}