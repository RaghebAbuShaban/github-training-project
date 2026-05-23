package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class DoctorProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 100;

    private Button btnEditProfile, btnLogout;
    private TextView tvProfileUsername;
    private ImageView ivProfilePhoto;
    private AppViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnProfileLogout);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        loadCurrentDoctor();

        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit().clear().apply();

                viewModel.setCurrentUser(null);

                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireActivity(), AuthActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });


        return view;
    }

    private void loadCurrentDoctor() {
        String username = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("username", null);

        tvProfileUsername.setText(username);

        if (username != null) {
            viewModel.getUserByUsername(username).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    viewModel.setCurrentUser(user);
                    if (user.getPhoto() != null) {
                        ivProfilePhoto.setImageBitmap(user.getPhoto());
                    }
                } else {
                    Toast.makeText(getContext(), "Doctor not found in database", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
                ivProfilePhoto.setImageBitmap(bitmap);

                User currentUser = viewModel.getCurrentUser().getValue();
                if (currentUser != null) {
                    currentUser.setPhoto(bitmap);
                    viewModel.updateUser(currentUser);
                    Toast.makeText(getContext(), "Profile photo updated", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editProfile() {
        User user = viewModel.getCurrentUser().getValue();
        if (user == null) {
            Toast.makeText(getContext(), "Doctor not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText edtUsername = dialogView.findViewById(R.id.edtDialogUsername);
        EditText edtPassword = dialogView.findViewById(R.id.edtDialogPassword);

        edtUsername.setText(user.getUsername());
        edtPassword.setText(user.getPassword());

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUsername = edtUsername.getText().toString().trim();
            String newPassword = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(newUsername) || TextUtils.isEmpty(newPassword)) {
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String oldUsername = user.getUsername();
            viewModel.updateDoctorNameEverywhere(oldUsername, newUsername);

            user.setUsername(newUsername);
            user.setPassword(newPassword);
            viewModel.updateUser(user);

            tvProfileUsername.setText(newUsername);

            requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .edit().putString("username", newUsername).apply();

            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}
