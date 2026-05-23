package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class PatientProfileFragment extends Fragment {

    private Button btnEditProfile, btnLogout;
    private TextView tvProfileUsername;
    private AppViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnProfileLogout);
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        loadCurrentPatient();

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User currentUser = viewModel.getCurrentUser().getValue();
                if (currentUser != null) {
                    showEditDialog(currentUser);
                } else {
                    Toast.makeText(getContext(), "Patient not loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                viewModel.setCurrentUser(null);

                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireActivity(), AuthActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }

    private void loadCurrentPatient() {
        Context context = requireContext();
        String username = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("username", null);

        tvProfileUsername.setText(username);

        if (username != null) {
            viewModel.getUserByUsername(username).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    viewModel.setCurrentUser(user);
                } else {
                    Toast.makeText(getContext(), "Patient not found in database", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog(User user) {
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

            viewModel.updatePatientNameEverywhere(oldUsername, newUsername);

            user.setUsername(newUsername);
            user.setPassword(newPassword);
            viewModel.updateUser(user);

            requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("username", newUsername)
                    .apply();

            tvProfileUsername.setText(newUsername);

            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
