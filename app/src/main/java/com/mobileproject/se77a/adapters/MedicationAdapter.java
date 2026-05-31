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
            
            // Format dosage with type: "500 mg · Comprimé · 3×/jour"
            String typeLabel = resolveTypeLabel(med.type);
            tvDosage.setText(med.dosage + " · " + typeLabel + " · " + med.frequency);
            
            // --- Next Dose Calculation ---
            String nextDose = resolveNextDose(med);
            if (med.takenToday) {
                tvNextDose.setText("Toutes les prises faites ✓");
            } else {
                tvNextDose.setText(nextDose.equals("--:--") ? "Prise si besoin" : "Prochaine prise : " + nextDose);
            }

            // ── Status badge ───────────────────────────────────────────────
            if (med.takenToday) {
                tvBadge.setText("Terminé ✓");
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
                case "syrup":     ivIcon.setImageResource(R.drawable.medicine);    break;
                case "injection": ivIcon.setImageResource(R.drawable.sering);      break;
                case "drops":     ivIcon.setImageResource(R.drawable.pellule);     break;
                default:          ivIcon.setImageResource(R.drawable.pellule); // tablet use pellule
            }

            // ── Active switch (silent set to avoid callback loop) ──────────
            switchActive.setOnCheckedChangeListener(null);
            switchActive.setChecked(med.isActive);
            switchActive.setOnCheckedChangeListener((btn, checked) ->
                    listener.onToggleActive(med, checked));

            // ── Mark taken button ──────────────────────────────────────────
            btnMarkTaken.setEnabled(!med.takenToday);
            btnMarkTaken.setAlpha(med.takenToday ? 0.4f : 1f);
            btnMarkTaken.setText(med.takenToday ? "✓ Pris" : "Marquer pris");
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

        private String resolveTypeLabel(String type) {
            if (type == null) return "Comprimé";
            switch (type) {
                case "syrup":     return "Sirop";
                case "injection": return "Injection";
                case "drops":     return "Gouttes";
                default:          return "Comprimé";
            }
        }

        private String resolveNextDose(Medication med) {
            if (med.reminderTime == null || med.reminderTime.isEmpty()) return "--:--";
            String[] allTimes = med.reminderTime.split(",");
            String[] takenTimes = (med.takenTimes != null && !med.takenTimes.isEmpty()) ? med.takenTimes.split(",") : new String[0];

            for (String time : allTimes) {
                boolean alreadyTaken = false;
                for (String taken : takenTimes) {
                    if (time.trim().equals(taken.trim())) {
                        alreadyTaken = true;
                        break;
                    }
                }
                if (!alreadyTaken) return time.trim();
            }
            return "Terminé";
        }
    }
}