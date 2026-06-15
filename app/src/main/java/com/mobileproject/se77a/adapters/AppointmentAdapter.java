package com.mobileproject.se77a.adapters;
import com.mobileproject.se77a.adapters.AppointmentAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments = new ArrayList<>();
    private final OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onDetailsClick(Appointment appointment);
    }

    public AppointmentAdapter(OnAppointmentClickListener listener) {
        this.listener = listener;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
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
        holder.tvCountdown.setText(computeCountdown(appt.date, appt.time));

        holder.btnDetails.setOnClickListener(v -> {
            if (listener != null) listener.onDetailsClick(appt);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    private String computeCountdown(String date, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date apptDate = sdf.parse(date + " " + time);
            if (apptDate == null) return "";

            long diffMs = apptDate.getTime() - System.currentTimeMillis();
            if (diffMs <= 0) return "maintenant";

            long diffH = diffMs / 3_600_000;
            if (diffH < 24) return "dans " + diffH + "h";

            long diffJ = diffH / 24;
            return "dans " + diffJ + "j";
        } catch (Exception e) {
            return "";
        }
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
            btnDetails = itemView.findViewById(R.id.btn_item_voir_details);
        }
    }
}