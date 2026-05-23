package com.example.myapplication;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentsFragment extends Fragment {

    private AppViewModel viewModel;
    private AppointmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_appointments, container, false);

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

            }
        });

        rvAppointments.setAdapter(adapter);
        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        // استعادة اسم المريض الحالي
        String patientName = null;

        if (viewModel.getCurrentUser().getValue() != null) {
            patientName = viewModel.getCurrentUser().getValue().getUsername();
        } else {
            // استعادة من SharedPreferences
            Context context = requireContext();
            patientName = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .getString("username", null);

            if (patientName != null) {
                String role = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .getString("role", "patient");
                String category = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .getString("category", null);
                viewModel.setCurrentUser(new User(patientName, "", role, category));
            }
        }

        if (patientName != null) {
            viewModel.getAppointmentsForPatient(patientName)
                    .observe(getViewLifecycleOwner(), appointments -> {
                        // فلترة المواعيد: فقط التي لم تنتهِ بعد
                        List<Appointment> activeAppointments = new ArrayList<>();
                        for (Appointment a : appointments) {
                            if (!"done".equals(a.getStatus())) {
                                activeAppointments.add(a);
                            }
                        }
                        adapter.setAppointments(activeAppointments);
                    });
        }

        return view;
    }
}
