package com.mobileproject.se77a.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;  // ← LinearLayout, pas TextView
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.Appointment;
import com.mobileproject.se77a.repository.AppointmentRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryBottomSheet extends BottomSheetDialogFragment {

    private final AppointmentRepository repository;

    public HistoryBottomSheet(AppointmentRepository repository) {
        this.repository = repository;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_history, container, false);

        RecyclerView  rv       = view.findViewById(R.id.rv_history);
        LinearLayout  tvEmpty  = view.findViewById(R.id.tv_history_empty); // ← corrigé
        ImageView     btnClose = view.findViewById(R.id.btn_close_history);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        btnClose.setOnClickListener(v -> dismiss());

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        repository.getAllAppointments().observe(getViewLifecycleOwner(), appointments -> {
            List<Appointment> history = new ArrayList<>();
            if (appointments != null) {
                for (Appointment appt : appointments) {
                    if (appt.date != null && appt.date.compareTo(today) < 0) {
                        history.add(appt);
                    } else if ("CANCELLED".equals(appt.status)) {
                        history.add(appt);
                    }
                }
            }

            if (history.isEmpty()) {
                rv.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE); // ← fonctionne sur LinearLayout aussi
            } else {
                rv.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
                rv.setAdapter(new HistoryAdapter(history));
            }
        });

        return view;
    }

    // ── Adapter interne ────────────────────────────────────────────────────
    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

        private final List<Appointment> list;

        HistoryAdapter(List<Appointment> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Appointment appt = list.get(position);

            h.tvDoctor.setText(appt.doctorName != null ? appt.doctorName : "—");
            h.tvSpecialty.setText(appt.specialty != null ? appt.specialty : "—");
            h.tvDateTime.setText((appt.date != null ? appt.date : "—")
                    + " à " + (appt.time != null ? appt.time : "—"));
            h.tvAddress.setText(appt.address != null ? appt.address : "—");

            boolean cancelled = "CANCELLED".equals(appt.status);
            h.tvStatus.setText(cancelled ? "Annulé" : "Terminé");
            h.tvStatus.setTextColor(ContextCompat.getColor(
                    h.itemView.getContext(),
                    cancelled ? R.color.red_error : R.color.green_done));

            h.viewIndicator.setBackgroundResource(
                    cancelled ? R.drawable.bg_med_pending : R.drawable.bg_med_checked);
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDoctor, tvSpecialty, tvDateTime, tvAddress, tvStatus;
            View     viewIndicator;

            VH(@NonNull View v) {
                super(v);
                tvDoctor      = v.findViewById(R.id.tv_history_doctor);
                tvSpecialty   = v.findViewById(R.id.tv_history_specialty);
                tvDateTime    = v.findViewById(R.id.tv_history_datetime);
                tvAddress     = v.findViewById(R.id.tv_history_address);
                tvStatus      = v.findViewById(R.id.tv_history_status);
                viewIndicator = v.findViewById(R.id.view_history_indicator);
            }
        }
    }
}