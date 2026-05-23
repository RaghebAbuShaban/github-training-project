package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<User> doctors = new ArrayList<>();
    private Context context;
    private boolean isDoctor;

    private OnDoctorActionListener listener;

    public interface OnDoctorActionListener {
        void onClickDoctor(User doctor);
            }

    /**
     * @param context
     * @param isDoctor
     * @param listener
     */
    public DoctorAdapter(Context context, boolean isDoctor, OnDoctorActionListener listener){
        this.context = context;
        this.isDoctor = isDoctor;
        this.listener = listener;
    }

    public void setDoctors(List<User> doctors){
        this.doctors = doctors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_actions, parent, false);
        return new DoctorViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        User d = doctors.get(position);
        holder.tvName.setText(d.getUsername());
        holder.tvCategory.setText(d.getCategory());

        Bitmap photo = d.getPhoto();
        if (photo != null) {
            holder.ivDoctor.setImageBitmap(photo);
        }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onClickDoctor(d);
                }
            });
    }



    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory;
        ImageView ivDoctor;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivDoctor = itemView.findViewById(R.id.ivDoctor);
        }
    }
}
