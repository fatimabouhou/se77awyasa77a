package com.mobileproject.se77a.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.entities.Appointment;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.viewmodels.HomeViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentHome extends Fragment {

    // ── Views ──────────────────────────────────────────────────────────────
    private TextView tvUsername, tvDate, tvAvatarInitials;
    private TextView tvMedProgress, tvRdvCount, tvOrdoCount;
    private TextView tvAlerteMedDetail, tvAlerteMedHeure;
    private TextView tvAlerteRdvDoctor, tvAlerteRdvSpecialty, tvAlerteRdvCountdown;

    private HomeViewModel       homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupViewModels();
        displayUserInfo();
        displayDate();

        // CHARGEMENT DU CONSEIL SANTE UNIQUEMENT SUR LA HOME
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.health_tip_container, new FragmentHealthTip())
                    .commit();
        }
    }

    // ── Bind ───────────────────────────────────────────────────────────────
    private void bindViews(View root) {
        tvUsername            = root.findViewById(R.id.tv_username);
        tvDate                = root.findViewById(R.id.tv_date);
        tvAvatarInitials      = root.findViewById(R.id.tv_avatar_initials);
        tvMedProgress         = root.findViewById(R.id.tv_med_progress);
        tvRdvCount            = root.findViewById(R.id.tv_rdv_count);
        tvOrdoCount           = root.findViewById(R.id.tv_ordo_count);
        tvAlerteMedDetail     = root.findViewById(R.id.tv_alerte_med_detail);
        tvAlerteMedHeure      = root.findViewById(R.id.tv_alerte_med_heure);
        tvAlerteRdvDoctor     = root.findViewById(R.id.tv_alerte_rdv_doctor);
        tvAlerteRdvSpecialty  = root.findViewById(R.id.tv_alerte_rdv_specialty);
        tvAlerteRdvCountdown  = root.findViewById(R.id.tv_alerte_rdv_countdown);
    }

    // ── ViewModels ─────────────────────────────────────────────────────────
    private void setupViewModels() {
        // MODIFICATION ICI : On utilise requireActivity() au lieu de this
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Résumé médicaments : Progrès en doses (Même logique que Tracking)
        homeViewModel.getMedProgressText().observe(getViewLifecycleOwner(), text -> {
            tvMedProgress.setText(text);
        });

        // RDV & ordonnances
        homeViewModel.getRdvThisMonth().observe(getViewLifecycleOwner(),
                count -> tvRdvCount.setText(String.valueOf(count != null ? count : 0)));

        homeViewModel.getOrdoCount().observe(getViewLifecycleOwner(),
                count -> tvOrdoCount.setText(String.valueOf(count != null ? count : 0)));

        // Alerte : prochain rendez-vous
        homeViewModel.getNextAppointment().observe(getViewLifecycleOwner(), appt -> {
            if (appt != null) {
                tvAlerteRdvDoctor.setText(appt.doctorName);
                tvAlerteRdvSpecialty.setText(appt.specialty);
                tvAlerteRdvCountdown.setText(appt.time);
                tvAlerteRdvCountdown.setVisibility(View.VISIBLE);
            } else {
                tvAlerteRdvDoctor.setText("Aucun rendez-vous");
                tvAlerteRdvSpecialty.setText("Planifiez votre suivi");
                tvAlerteRdvCountdown.setVisibility(View.GONE);
            }
        });

        // Alerte : prochain médicament actif non pris
        homeViewModel.activeMedications.observe(getViewLifecycleOwner(), this::updateMedAlert);
    }

    // ── Alerte médicament ──────────────────────────────────────────────────
    /**
     * Trouve le prochain rappel parmi les médicaments actifs non pris aujourd'hui
     * et met à jour les vues d'alerte.
     */
    private void updateMedAlert(List<Medication> medications) {
        if (medications == null || medications.isEmpty()) {
            tvAlerteMedDetail.setText("Aucun médicament actif");
            tvAlerteMedHeure.setText("–");
            return;
        }

        String nextMedName = null;
        String nextTime    = null;

        for (Medication med : medications) {
            if (med.takenToday) continue;
            if (med.reminderTime == null || med.reminderTime.isEmpty()) continue;

            String[] allDoses = med.reminderTime.split(",");
            String takenStr = (med.takenTimes != null) ? med.takenTimes : "";
            
            for (String t : allDoses) {
                String time = t.trim();
                if (time.isEmpty()) continue;

                // Si cette dose est déjà prise, on passe à la suivante
                if (takenStr.contains(time)) continue;

                // On cherche la dose la plus ancienne parmi celles qui restent à prendre aujourd'hui
                if (nextTime == null || compareTime(time, nextTime) < 0) {
                    nextTime = time;
                    nextMedName = med.name;
                }
            }
        }

        if (nextMedName != null) {
            tvAlerteMedDetail.setText("Prochain rappel : " + nextMedName);
            tvAlerteMedHeure.setText(nextTime);
        } else {
            // Tous pris ou aucun rappel restant aujourd'hui
            tvAlerteMedDetail.setText("Tous les médicaments pris ✓");
            tvAlerteMedHeure.setText("–");
        }
    }

    /** Compare deux horaires "HH:mm". Retourne négatif si a < b. */
    private int compareTime(String a, String b) {
        return a.replace(":", "").compareTo(b.replace(":", ""));
    }

    // ── Utilisateur ────────────────────────────────────────────────────────
    private void displayUserInfo() {
        // Lit le nom depuis les SharedPreferences (clé "username" mise à jour au login)
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        String username = prefs.getString("current_user_name", "Utilisateur");

        tvUsername.setText(username);
        // Initiales : première lettre de chaque mot (max 2)
        String[] parts    = username.trim().split("\\s+");
        StringBuilder ini = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) ini.append(parts[i].charAt(0));
        }
        tvAvatarInitials.setText(ini.toString().toUpperCase(Locale.getDefault()));
    }

    // ── Date ───────────────────────────────────────────────────────────────
    private void displayDate() {
        // Ex : "Mardi 3 juin"
        SimpleDateFormat fmt = new SimpleDateFormat("EEEE d MMMM", new Locale("fr", "FR"));
        String date = fmt.format(new Date());
        // Capitalize first letter
        if (!date.isEmpty()) {
            date = date.substring(0, 1).toUpperCase(Locale.getDefault()) + date.substring(1);
        }
        tvDate.setText(date);
    }
}