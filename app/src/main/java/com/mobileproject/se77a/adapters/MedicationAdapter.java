package com.mobileproject.se77a.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.Medication;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedViewHolder> {

    // ── Listener interface (implemented by the Fragment) ───────────────────
    public interface OnMedicationClickListener {
        void onMedicationClick(Medication medication);
        void onToggleActive(Medication medication, boolean isActive);
        void onMarkTaken(Medication medication);
        void onDeleteMedication(Medication medication);
    }

    private List<Medication>           items = new ArrayList<>();
    private final OnMedicationClickListener listener;

    public MedicationAdapter(OnMedicationClickListener listener) {
        this.listener = listener;
    }

    // ── Update data ────────────────────────────────────────────────────────
    public void setMedications(List<Medication> medications) {
        this.items = medications;
        notifyDataSetChanged();
    }

    // ── RecyclerView boilerplate ───────────────────────────────────────────
    @NonNull
    @Override
    public MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication, parent, false);
        return new MedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MedViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ── ViewHolder ─────────────────────────────────────────────────────────
    class MedViewHolder extends RecyclerView.ViewHolder {

        ImageView      ivIcon;
        TextView       tvName, tvDosage, tvBadge, tvNextDose;
        SwitchCompat   switchActive;
        MaterialButton btnMarkTaken, btnPhoto, btnCall;
        LinearLayout   llPhotoRow;

        MedViewHolder(@NonNull View v) {
            super(v);
            ivIcon       = v.findViewById(R.id.iv_med_icon);
            tvName       = v.findViewById(R.id.tv_med_name);
            tvDosage     = v.findViewById(R.id.tv_med_dosage);
            tvBadge      = v.findViewById(R.id.tv_status_badge);
            tvNextDose   = v.findViewById(R.id.tv_next_dose_time);
            switchActive = v.findViewById(R.id.switch_reminder);
            btnMarkTaken = v.findViewById(R.id.btn_mark_taken);
            llPhotoRow   = v.findViewById(R.id.ll_photo_row);
            btnPhoto     = v.findViewById(R.id.btn_take_photo);
            btnCall      = v.findViewById(R.id.btn_call_doctor);
        }

        void bind(Medication med) {
            Context ctx = itemView.getContext();

            // ── Text ───────────────────────────────────────────────────────
            tvName.setText(med.name);
            tvDosage.setText(med.dosage + " · " + med.frequency);
            tvNextDose.setText(med.reminderTime != null && !med.reminderTime.isEmpty()
                    ? "Prochaine prise : " + med.reminderTime
                    : "Prise si besoin");

            // ── Status badge ───────────────────────────────────────────────
            if (med.takenToday) {
                tvBadge.setText("Pris ✓");
                tvBadge.setBackgroundResource(R.drawable.bg_badge_taken);
            } else if (med.isActive) {
                tvBadge.setText("Actif");
                tvBadge.setBackgroundResource(R.drawable.bg_badge_active);
            } else {
                tvBadge.setText("Inactif");
                tvBadge.setBackgroundResource(R.drawable.bg_badge_inactive);
            }

            // ── Type icon ──────────────────────────────────────────────────
            switch (med.type == null ? "tablet" : med.type) {
                case "syrup":     ivIcon.setImageResource(android.R.drawable.ic_menu_manage);    break;
                case "injection": ivIcon.setImageResource(android.R.drawable.ic_menu_edit);      break;
                case "drops":     ivIcon.setImageResource(android.R.drawable.ic_menu_zoom);      break;
                default:          ivIcon.setImageResource(android.R.drawable.ic_menu_info_details);
            }

            // ── Active switch (silent set to avoid callback loop) ──────────
            switchActive.setOnCheckedChangeListener(null);
            switchActive.setChecked(med.isActive);
            switchActive.setOnCheckedChangeListener((btn, checked) ->
                    listener.onToggleActive(med, checked));

            // ── Mark taken button ──────────────────────────────────────────
            btnMarkTaken.setEnabled(!med.takenToday);
            btnMarkTaken.setAlpha(med.takenToday ? 0.4f : 1f);
            btnMarkTaken.setText(med.takenToday ? "✓ Pris" : "Pris ✓");
            btnMarkTaken.setOnClickListener(v -> listener.onMarkTaken(med));

            // ── Long press → reveal photo / call row ───────────────────────
            llPhotoRow.setVisibility(View.GONE);
            itemView.setOnLongClickListener(v -> {
                boolean shown = llPhotoRow.getVisibility() == View.VISIBLE;
                llPhotoRow.setVisibility(shown ? View.GONE : View.VISIBLE);
                return true;
            });

            // ── Photo & call placeholders ──────────────────────────────────
            btnPhoto.setOnClickListener(v -> listener.onMedicationClick(med)); // photo handled in fragment
            btnCall.setOnClickListener(v  -> listener.onMedicationClick(med)); // call handled in fragment

            // ── Card click ─────────────────────────────────────────────────
            itemView.setOnClickListener(v -> listener.onMedicationClick(med));
        }
    }
}