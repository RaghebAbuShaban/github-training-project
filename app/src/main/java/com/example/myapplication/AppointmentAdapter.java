package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments = new ArrayList<>();
    private Context context;
    private boolean isDoctor;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onEdit(Appointment appointment);
        void onDelete(Appointment appointment);
        void onUpdateStatus(Appointment appointment);
        void onClick(Appointment appointment);
    }

    public AppointmentAdapter(Context context, boolean isDoctor, OnAppointmentActionListener listener){
        this.context = context;
        this.isDoctor = isDoctor;
        this.listener = listener;
    }

    public void setAppointments(List<Appointment> appointments){
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.tvDate.setText(appointment.getDate());
        holder.tvTime.setText(appointment.getTime());
        holder.tvPatient.setText("Patient: " + (appointment.getPatientName() != null ? appointment.getPatientName() : "N/A"));
        holder.tvStatus.setText("Status: " + appointment.getStatus());

        if (isDoctor == true ){
            holder.btnEditAppintment.setVisibility(View.VISIBLE);
            holder.btnDeleteAppintment.setVisibility(View.VISIBLE);
            if ("done".equals(appointment.getStatus())){
                holder.btnEditAppintment.setVisibility(View.GONE);
                holder.btnDeleteAppintment.setVisibility(View.GONE);

            }


            holder.btnEditAppintment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEdit(appointment);
                }
            });

            holder.btnDeleteAppintment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDelete(appointment);
                }
            });
        }else {
            holder.tvPatient.setVisibility(View.INVISIBLE);
            holder.tvDoctor.setVisibility(View.VISIBLE);
            holder.tvDoctor.setText("Doctor: " + appointment.getDoctorName() );

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(appointment);
                }

                if (isDoctor && "booked".equals(appointment.getStatus())) {
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setTitle("Mark as Done")
                            .setMessage("Has the patient finished the appointment?")
                            .setPositiveButton("Yes", (dialog, which) -> listener.onUpdateStatus(appointment))
                            .setNegativeButton("No", null)
                            .show();
                }
                else if (!isDoctor) {
                    listener.onClick(appointment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvPatient, tvStatus, tvDoctor;

        ImageView btnEditAppintment, btnDeleteAppintment;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPatient = itemView.findViewById(R.id.tvPatientName);
            tvDoctor = itemView.findViewById(R.id.tvDoctorName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEditAppintment = itemView.findViewById(R.id.btnEditAppintment);
            btnDeleteAppintment = itemView.findViewById(R.id.btnDeleteAppintment);
        }
    }
}
