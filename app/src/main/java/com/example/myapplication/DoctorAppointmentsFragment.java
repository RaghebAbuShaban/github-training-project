package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DoctorAppointmentsFragment extends Fragment {

    private AppViewModel viewModel;
    private AppointmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_appointments, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.rvAppointments);
        Button btnAdd = v.findViewById(R.id.btnAddAppointment);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        adapter = new AppointmentAdapter(getContext(), true, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onEdit(Appointment appointment) {
                showEditAppointmentDialog(appointment);
            }

            @Override
            public void onDelete(Appointment appointment) {
                viewModel.deleteAppointment(appointment);
            }
            @Override
            public void onUpdateStatus(Appointment appointment) {
                showMedicalRecordDialog(appointment);
            }

            @Override
            public void onClick(Appointment appointment) {

            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (viewModel.getCurrentUser().getValue() == null) {
            Context context = requireContext();
            String username = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .getString("username", null);
            String role = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .getString("role", "doctor");
            String category = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    .getString("category", null);

            if (username != null) {
                viewModel.setCurrentUser(new User(username, "", role, category));
            }
        }

        if (viewModel.getCurrentUser().getValue() != null) {
            String doctorName = viewModel.getCurrentUser().getValue().getUsername();
            viewModel.getAppointmentsForDoctor(doctorName)
                    .observe(getViewLifecycleOwner(), appointments -> adapter.setAppointments(appointments));
        }


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAppointmentDialog();
            }
        });
        return v;
    }

    private void showAddAppointmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Appointment");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_appointment, null);
        EditText edtDate = dialogView.findViewById(R.id.edtDate);
        EditText edtTime = dialogView.findViewById(R.id.edtTime);

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(getContext(),
                        (picker, year, month, dayOfMonth) ->
                                edtDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                new android.app.TimePickerDialog(getContext(),
                        (picker, hourOfDay, minute) ->
                                edtTime.setText(String.format("%02d:%02d", hourOfDay, minute)),
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
                        .show();
            }
        });


        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String date = edtDate.getText().toString().trim();
            String time = edtTime.getText().toString().trim();

            if (date.isEmpty() || time.isEmpty()) {
                Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                return;
            }

            String username = "";
            if (viewModel.getCurrentUser().getValue() != null) {
                username = viewModel.getCurrentUser().getValue().getUsername();
            }

            viewModel.insertAppointment(new Appointment(username, null, date, time, "pending"));
        });

        builder.show();
    }

    private void showMedicalRecordDialog(Appointment appointment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Medical Record");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_medical_record, null);
        EditText edtDiagnosis = dialogView.findViewById(R.id.edtDiagnosis);
        EditText edtTreatment = dialogView.findViewById(R.id.edtTreatment);
        EditText edtNotes = dialogView.findViewById(R.id.edtNotes);

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String diagnosis = edtDiagnosis.getText().toString().trim();
            String treatment = edtTreatment.getText().toString().trim();
            String notes = edtNotes.getText().toString().trim();

            if (diagnosis.isEmpty() || treatment.isEmpty()) {
                Toast.makeText(getContext(), "Diagnosis and treatment are required", Toast.LENGTH_SHORT).show();
                return;
            }

            appointment.setStatus("done");
            viewModel.updateAppointment(appointment);

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            MedicalRecord record = new MedicalRecord(
                    appointment.getPatientName(),
                    appointment.getDoctorName(),
                    diagnosis,
                    treatment,
                    notes,
                    date
            );
            viewModel.insertMedicalRecord(record);

            Toast.makeText(getContext(), "Medical record added", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    private void showEditAppointmentDialog(Appointment appointment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Appointment");
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_appointment,null);
        EditText edDate = view.findViewById(R.id.edDate);
        EditText edTime = view.findViewById(R.id.edTime);

        edDate.setText(appointment.getDate());
        edTime.setText(appointment.getTime());

        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(getContext(),
                        (datePicker, year, month, dayOfMonth) ->
                                edDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        edTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                new android.app.TimePickerDialog(getContext(),
                        (timePicker, hourOfDay, minute) ->
                                edTime.setText(String.format("%02d:%02d", hourOfDay, minute)),
                        cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
                        .show();
            }
        });


        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String date = edDate.getText().toString().trim();
            String time = edTime.getText().toString().trim();

            appointment.setDate(date);
            appointment.setTime(time);

            viewModel.updateAppointment(appointment);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {});
        builder.show();

    }



}
