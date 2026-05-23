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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class LoginFragment extends Fragment {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private AppViewModel viewModel;

    public static User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        edtUsername = view.findViewById(R.id.edtLoginUsername);
        edtPassword = view.findViewById(R.id.edtLoginPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.loginUser(username, password).observe(getViewLifecycleOwner(), user -> {
                    if (user == null) {
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    } else if (!user.getPassword().equals(password)) {
                        Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    } else {
                        Context context = requireContext();
                        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean("logged_in", true)
                                .putString("username", user.getUsername())
                                .putString("role", user.getType())
                                .putString("category", user.getCategory())
                                .apply();

                        viewModel.setCurrentUser(user);

                        Toast.makeText(getContext(), "Login successful as " + user.getType(), Toast.LENGTH_SHORT).show();

                        if (user.getType().equals("doctor")) {
                            startActivity(new Intent(requireActivity(), DoctorMainActivity.class));
                        } else {
                            startActivity(new Intent(requireActivity(), PatientMainActivity.class));
                        }

                        requireActivity().finish();
                    }
                });
            }
        });

        return view;
    }

}
