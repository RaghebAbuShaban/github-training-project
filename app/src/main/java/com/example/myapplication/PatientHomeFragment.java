package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PatientHomeFragment extends Fragment {

    private RecyclerView rvDoctors;
    private DoctorAdapter adapter;
    private AppViewModel viewModel;
    private List<User> allDoctors = new ArrayList<>();

    private Button btnAll, btnGeneral, btnDental, btnDermatology;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_home, container, false);

        rvDoctors = view.findViewById(R.id.rvDoctors);
        btnAll = view.findViewById(R.id.btnAll);
        btnGeneral = view.findViewById(R.id.btnGeneral);
        btnDental = view.findViewById(R.id.btnDental);
        btnDermatology = view.findViewById(R.id.btnDermatology);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new DoctorAdapter(getContext(), false, new DoctorAdapter.OnDoctorActionListener() {
            @Override
            public void onClickDoctor(User doctor) {
                PatientDoctorAppointmentsFragment fragment =
                        PatientDoctorAppointmentsFragment.newInstance(doctor.getUsername());
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rvDoctors.setAdapter(adapter);
        rvDoctors.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getUsersByType("doctor").observe(getViewLifecycleOwner(), doctors -> {
            if (doctors != null) {
                allDoctors.clear();
                allDoctors.addAll(doctors);
                adapter.setDoctors(allDoctors);
            }
        });

        View.OnClickListener filterListener = v -> {
            String category = ((Button)v).getText().toString();
            filterDoctors(category);
        };

        btnAll.setOnClickListener(filterListener);
        btnGeneral.setOnClickListener(filterListener);
        btnDental.setOnClickListener(filterListener);
        btnDermatology.setOnClickListener(filterListener);

        return view;
    }

    private void filterDoctors(String category) {
        if (category.equals("All")) {
            adapter.setDoctors(allDoctors);
            return;
        }

        List<User> filtered = new ArrayList<>();
        for (User doctor : allDoctors) {
            if (doctor.getCategory() != null && doctor.getCategory().equalsIgnoreCase(category)) {
                filtered.add(doctor);
            }
        }
        adapter.setDoctors(filtered);
    }
}
