package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class BookAppointmentFragment extends Fragment {

    private static final String ARG_APPOINTMENT = "appointment";

    private Appointment appointment;
    private AppViewModel viewModel;

    public static BookAppointmentFragment newInstance(Appointment appointment) {
        BookAppointmentFragment fragment = new BookAppointmentFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_APPOINTMENT, appointment);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_appointment, container, false);

        TextView tvDoctorName = view.findViewById(R.id.tvDoctorName);
        TextView tvDateTime = view.findViewById(R.id.tvDateTime);
        Button btnBook = view.findViewById(R.id.btnBookAppointment);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        if (getArguments() != null) {
            appointment = (Appointment) getArguments().getSerializable(ARG_APPOINTMENT);
        }

        if (appointment != null) {
            tvDoctorName.setText("Doctor: " + appointment.getDoctorName());
            tvDateTime.setText("Date & Time: " + appointment.getDate() + " " + appointment.getTime());
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewModel.getCurrentUser().getValue() == null) {
                    Context context = requireContext();
                    String username = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .getString("username", null);
                    String role = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .getString("role", "patient");
                    String category = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .getString("category", null);

                    if (username != null) {
                        viewModel.setCurrentUser(new User(username, "", role, category));
                    } else {
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                appointment.setPatientName(viewModel.getCurrentUser().getValue().getUsername());
                appointment.setStatus("booked");

                viewModel.updateAppointment(appointment);

                Toast.makeText(getContext(), "Appointment booked successfully!", Toast.LENGTH_SHORT).show();

                Context context = requireContext();
                createNotificationChannel(context);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "appointment_channel")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Appointment Booked")
                        .setContentText("Your appointment with " + appointment.getDoctorName() + " is booked successfully.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                        && ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Notification permission is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());

                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "appointment_channel";
            String channelName = "Appointment Notifications";
            String channelDesc = "Notifications for booked appointments";
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;

            android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDesc);

            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
