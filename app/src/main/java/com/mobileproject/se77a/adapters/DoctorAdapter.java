package com.mobileproject.se77a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.Doctor;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.VH> {

    public interface OnDoctorClick { void onClick(Doctor doctor); }

    private final List<Doctor>  doctors;
    private final OnDoctorClick listener;

    public DoctorAdapter(List<Doctor> doctors, OnDoctorClick listener) {
        this.doctors  = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Doctor doc = doctors.get(position);
        holder.tvName.setText(doc.name);
        holder.tvSpecialty.setText(doc.specialty);
        holder.tvAddress.setText(doc.address);
        holder.itemView.setOnClickListener(v -> listener.onClick(doc));
    }

    @Override
    public int getItemCount() { return doctors.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty, tvAddress;
        VH(View v) {
            super(v);
            tvName      = v.findViewById(R.id.tv_doctor_name);
            tvSpecialty = v.findViewById(R.id.tv_doctor_specialty);
            tvAddress   = v.findViewById(R.id.tv_doctor_address);
        }
    }
}