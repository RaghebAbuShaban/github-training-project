package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class RegisterFragment extends Fragment {

    private EditText edtUsername, edtPassword;
    private Button btnRegister;
    private RadioGroup rgUserType;
    private Spinner spinnerCategories;
    private AppViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        edtUsername = view.findViewById(R.id.edtRegUsername);
        edtPassword = view.findViewById(R.id.edtRegPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        rgUserType = view.findViewById(R.id.roleGroup);
        spinnerCategories = view.findViewById(R.id.spinnerCategories);

        String[] doctorCategories = {"General", "Dental", "Dermatology"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                doctorCategories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDoctor) {
                spinnerCategories.setVisibility(View.VISIBLE);
            } else {
                spinnerCategories.setVisibility(View.GONE);
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = rgUserType.getCheckedRadioButtonId() == R.id.rbDoctor ? "doctor" : "patient";

                String category = null;
                if (type.equals("doctor")) {
                    category = spinnerCategories.getSelectedItem().toString();
                }

                User newUser = new User(username, password, type, category);

                viewModel.registerUser(newUser);

                LoginFragment.currentUser = newUser;

                SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE);
                prefs.edit()
                        .putBoolean("logged_in", true)
                        .putString("username", newUser.getUsername())
                        .putString("role", newUser.getType())
                        .putString("category", newUser.getCategory()) // حفظ الكاتيجوري كمان
                        .apply();

                Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();

                if (type.equals("doctor")) {
                    startActivity(new Intent(requireActivity(), DoctorMainActivity.class));
                } else {
                    startActivity(new Intent(requireActivity(), PatientMainActivity.class));
                }
                requireActivity().finish();
            }
        });

        return view;
    }
}
