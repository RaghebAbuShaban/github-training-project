package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PatientMedicalRecordFragment extends Fragment {

    private AppViewModel viewModel;
    private AppointmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_medical_record, container, false);

        RecyclerView rvRecords = view.findViewById(R.id.rvMedicalRecords);
        adapter = new AppointmentAdapter(getContext(), false, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onEdit(Appointment appointment) { }

            @Override
            public void onDelete(Appointment appointment) { }

            @Override
            public void onUpdateStatus(Appointment appointment) { }

            @Override
            public void onClick(Appointment appointment) {
                showMedicalRecordDialog(appointment);
            }
        });

        rvRecords.setAdapter(adapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        String patientName = null;

        if (viewModel.getCurrentUser().getValue() != null) {
            patientName = viewModel.getCurrentUser().getValue().getUsername();
        } else {
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
                        List<Appointment> doneAppointments = new ArrayList<>();
                        for (Appointment a : appointments) {
                            if ("done".equals(a.getStatus())) {
                                doneAppointments.add(a);
                            }
                        }
                        adapter.setAppointments(doneAppointments);
                    });
        }

        return view;
    }

    private void showMedicalRecordDialog(Appointment appointment) {
        String patientName = appointment.getPatientName();
        String doctorName = appointment.getDoctorName();

        viewModel.getMedicalRecordForPatientAndDoctor(patientName, doctorName)
                .observe(getViewLifecycleOwner(), records -> {
                    if (records != null && !records.isEmpty()) {
                        MedicalRecord record = records.get(0);

                        View dialogView = LayoutInflater.from(getContext())
                                .inflate(R.layout.dialog_medical_record, null);
                        TextView tvDiagnosis = dialogView.findViewById(R.id.tvDiagnosis);
                        TextView tvTreatment = dialogView.findViewById(R.id.tvTreatment);
                        TextView tvNotes = dialogView.findViewById(R.id.tvNotes);

                        tvDiagnosis.setText(record.getDiagnosis());
                        tvTreatment.setText(record.getTreatment());
                        tvNotes.setText(record.getNotes());

                        new AlertDialog.Builder(getContext())
                                .setTitle("Medical Record")
                                .setView(dialogView)
                                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        Toast.makeText(getContext(), "No medical record found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
