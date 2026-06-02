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
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.viewmodels.HomeViewModel;
import com.mobileproject.se77a.viewmodels.MedicationViewModel;

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
    private TextView tvConseilTitre, tvConseilTexte;

    private HomeViewModel       homeViewModel;
    private MedicationViewModel medicationViewModel;

    // ── Conseils santé du jour (rotatifs par index du jour) ───────────────
    private static final String[][] CONSEILS = {
            {"Hydratation",       "Pensez à boire 1,5 L d'eau par jour, surtout en période de chaleur."},
            {"Activité physique", "30 minutes de marche par jour réduisent les risques cardiovasculaires."},
            {"Sommeil",           "7 à 9 heures de sommeil sont recommandées pour un adulte en bonne santé."},
            {"Alimentation",      "Favorisez les fruits et légumes frais à chaque repas de la journée."},
            {"Stress",            "Pratiquez 5 minutes de respiration profonde pour réduire votre stress."},
            {"Posture",           "Pensez à vous lever et vous étirer toutes les heures si vous êtes assis."},
            {"Médicaments",       "Prenez vos médicaments à heure fixe pour maximiser leur efficacité."},
    };

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
        displayConseil();
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
        tvConseilTitre        = root.findViewById(R.id.tv_conseil_titre);
        tvConseilTexte        = root.findViewById(R.id.tv_conseil_texte);
    }

    // ── ViewModels ─────────────────────────────────────────────────────────
    private void setupViewModels() {
        // MODIFICATION ICI : On utilise requireActivity() au lieu de this
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        medicationViewModel = new ViewModelProvider(requireActivity()).get(MedicationViewModel.class);

        // Résumé médicaments (Optimisé pour éviter le bug du 0/0)
        homeViewModel.activeMedCount.observe(getViewLifecycleOwner(), active -> {
            updateProgressText(homeViewModel.takenTodayCount.getValue(), active);
        });

        homeViewModel.takenTodayCount.observe(getViewLifecycleOwner(), taken -> {
            updateProgressText(taken, homeViewModel.activeMedCount.getValue());
        });

        // RDV & ordonnances
        homeViewModel.getRdvThisMonth().observe(getViewLifecycleOwner(),
                count -> tvRdvCount.setText(String.valueOf(count != null ? count : 0)));

        homeViewModel.getOrdoCount().observe(getViewLifecycleOwner(),
                count -> tvOrdoCount.setText(String.valueOf(count != null ? count : 0)));

        // Alerte : prochain médicament actif non pris
        homeViewModel.activeMedications.observe(getViewLifecycleOwner(), this::updateMedAlert);
    }

    // AJOUTE cette petite méthode d'aide juste en dessous de setupViewModels
    private void updateProgressText(Integer taken, Integer active) {
        int t = (taken != null) ? taken : 0;
        int a = (active != null) ? active : 0;
        tvMedProgress.setText(String.format(Locale.getDefault(), "%d/%d", t, a));
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

        String nowHHmm = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String nextMedName = null;
        String nextTime    = null;

        for (Medication med : medications) {
            if (med.takenToday) continue;
            if (med.reminderTime == null || med.reminderTime.isEmpty()) continue;

            for (String t : med.reminderTime.split(",")) {
                String time = t.trim();
                // Prendre le premier horaire >= maintenant
                if (nextTime == null || compareTime(time, nowHHmm) >= 0 &&
                        (compareTime(time, nextTime) < 0)) {
                    nextTime    = time;
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

    // ── Conseil du jour ───────────────────────────────────────────────────
    private void displayConseil() {
        // Index basé sur le jour de l'année pour rotation stable
        int dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR);
        String[] conseil = CONSEILS[dayOfYear % CONSEILS.length];
        tvConseilTitre.setText(conseil[0]);
        tvConseilTexte.setText(conseil[1]);
    }
}