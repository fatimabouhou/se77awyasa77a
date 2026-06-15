package com.mobileproject.se77a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.Appointment;
import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments = new ArrayList<>();
    private OnAppointmentClickListener clickListener;

    // Interface pour intercepter le clic dans le Fragment
    public interface OnAppointmentClickListener {
        void onDetailsClick(Appointment appointment);
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    public void setOnAppointmentClickListener(OnAppointmentClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appt = appointments.get(position);

        holder.tvDoctor.setText(appt.doctorName);
        holder.tvSpecialty.setText(appt.specialty);
        holder.tvDate.setText(appt.date + " · " + appt.time);

        // Note: Si vous voulez un compte à rebours dynamique par item, il faudra
        // lui passer votre méthode utilitaire. En attendant, on met une valeur textuelle.
        holder.tvCountdown.setText("À venir");

        // Gestion dynamique du clic sur le bouton Détails de CET item précis
        holder.btnDetails.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDetailsClick(appt);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctor, tvSpecialty, tvDate, tvCountdown;
        CardView btnDetails;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctor = itemView.findViewById(R.id.tv_item_doctor);
            tvSpecialty = itemView.findViewById(R.id.tv_item_specialty);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            tvCountdown = itemView.findViewById(R.id.tv_item_countdown);
            btnDetails = itemView.findViewById(R.id.btn_item_details);
        }
    }
}