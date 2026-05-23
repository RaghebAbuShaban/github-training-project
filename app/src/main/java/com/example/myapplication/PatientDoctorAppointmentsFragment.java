package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PatientDoctorAppointmentsFragment extends Fragment {

    private static final String ARG_DOCTOR_NAME = "doctor_name";
    private String doctorName;

    private AppViewModel viewModel;
    private AppointmentAdapter adapter;

    public static PatientDoctorAppointmentsFragment newInstance(String doctorName) {
        PatientDoctorAppointmentsFragment fragment = new PatientDoctorAppointmentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DOCTOR_NAME, doctorName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctorName = getArguments().getString(ARG_DOCTOR_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_doctor_appointments, container, false);

        RecyclerView rvAppointments = view.findViewById(R.id.rvAppointments);
        adapter = new AppointmentAdapter(getContext(), false, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onEdit(Appointment appointment) { }

            @Override
            public void onDelete(Appointment appointment) { }

            @Override
            public void onUpdateStatus(Appointment appointment) {

            }

            @Override
            public void onClick(Appointment appointment) {
                BookAppointmentFragment fragment = BookAppointmentFragment.newInstance(appointment);
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rvAppointments.setAdapter(adapter);
        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        if (doctorName != null) {
            viewModel.getAvailableAppointmentsForDoctor(doctorName)
                    .observe(getViewLifecycleOwner(), appointments -> adapter.setAppointments(appointments));
        }

        return view;
    }
}
