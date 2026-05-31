package com.mobileproject.se77a.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FragmentHome extends Fragment {

    // ============================================================
    // VUES
    // ============================================================
    private TextView tvUsername, tvDate, tvAvatarInitials;
    private TextView tvNextDoctor, tvNextSpecialty, tvCountdown;
    private TextView tvMedProgress, tvNextMedReminder;
    private TextView tvOrdoCount, tvOrdoStatus;
    private CardView cardNextRdv, cardMedications, cardOrdonnances;
    private CardView btnVoirSuivi, btnSuiviComplet;

    // ============================================================
    // DONNÉES STATIQUES (à remplacer par SQLite/API plus tard)
    // ============================================================

    // Prochain RDV
    private static final String RDV_DOCTOR    = "Dr. Ahmed Benali";
    private static final String RDV_SPECIALTY = "Cardiologue · Demain 14h30";
    private static final long   RDV_TIMESTAMP = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(18);

    // Médicaments : prises du jour
    private static final int MED_PRIS  = 2;   // nombre de prises déjà effectuées
    private static final int MED_TOTAL = 3;   // total de prises prévues aujourd'hui
    private static final String MED_PROCHAIN_RAPPEL = "⏰ Soir 20h00";

    // Ordonnances
    private static final int ORDO_COUNT = 5;

    // ============================================================
    // CYCLE DE VIE
    // ============================================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        loadUserData();
        loadRdvData();
        loadMedicamentData();
        loadOrdonnanceData();
        setupClickListeners();

        return view;
    }

    // ============================================================
    // INITIALISATION DES VUES
    // ============================================================
    private void initViews(View view) {
        tvUsername         = view.findViewById(R.id.tv_username);
        tvDate             = view.findViewById(R.id.tv_date);
        tvAvatarInitials   = view.findViewById(R.id.tv_avatar_initials);

        tvNextDoctor       = view.findViewById(R.id.tv_next_doctor);
        tvNextSpecialty    = view.findViewById(R.id.tv_next_specialty);
        tvCountdown        = view.findViewById(R.id.tv_countdown);
        cardNextRdv        = view.findViewById(R.id.card_next_rdv);

        tvMedProgress      = view.findViewById(R.id.tv_med_progress);
        tvNextMedReminder  = view.findViewById(R.id.tv_next_med_reminder);
        cardMedications    = view.findViewById(R.id.card_medications);

        tvOrdoCount        = view.findViewById(R.id.tv_ordo_count);
        tvOrdoStatus       = view.findViewById(R.id.tv_ordo_status);
        cardOrdonnances    = view.findViewById(R.id.card_ordonnances);

        btnVoirSuivi       = view.findViewById(R.id.btn_voir_suivi);
        btnSuiviComplet    = view.findViewById(R.id.btn_suivi_complet);
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES UTILISATEUR (SharedPreferences)
    // ============================================================
    private void loadUserData() {
        if (getContext() == null) return;

        try {
            SharedPreferences prefs = getContext()
                    .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String fullName = prefs.getString("current_user_name", "Utilisateur");

            if (tvUsername != null) tvUsername.setText(fullName);

            // Calcul des initiales
            String initials = "U";
            if (fullName != null && !fullName.trim().isEmpty()) {
                String[] parts = fullName.trim().split("\\s+");
                if (parts.length >= 2) {
                    initials = String.valueOf(parts[0].charAt(0)).toUpperCase()
                            + String.valueOf(parts[1].charAt(0)).toUpperCase();
                } else {
                    initials = String.valueOf(parts[0].charAt(0)).toUpperCase();
                }
            }
            if (tvAvatarInitials != null) tvAvatarInitials.setText(initials);

            // Date du jour en français
            String today = new SimpleDateFormat("EEEE d MMMM", Locale.FRENCH).format(new Date());
            String dateFormatted = today.substring(0, 1).toUpperCase() + today.substring(1);
            if (tvDate != null) tvDate.setText(dateFormatted);

        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur loadUserData: " + e.getMessage());
        }
    }

    // ============================================================
    // CHARGEMENT DU PROCHAIN RDV
    // Remplacer les constantes par une requête SQLite :
    //   SELECT * FROM rendez_vous WHERE date >= NOW() ORDER BY date ASC LIMIT 1
    // ============================================================
    private void loadRdvData() {
        try {
            if (tvNextDoctor != null)    tvNextDoctor.setText(RDV_DOCTOR);
            if (tvNextSpecialty != null) tvNextSpecialty.setText(RDV_SPECIALTY);

            // Calcul du compte à rebours
            long diffMs    = RDV_TIMESTAMP - System.currentTimeMillis();
            long diffHours = TimeUnit.MILLISECONDS.toHours(diffMs);
            long diffDays  = TimeUnit.MILLISECONDS.toDays(diffMs);

            String countdown;
            if (diffMs < 0) {
                countdown = "Passé";
            } else if (diffHours < 24) {
                countdown = "dans " + diffHours + "h";
            } else {
                countdown = "dans " + diffDays + " jour" + (diffDays > 1 ? "s" : "");
            }
            if (tvCountdown != null) tvCountdown.setText(countdown);

        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur loadRdvData: " + e.getMessage());
        }
    }

    // ============================================================
    // CHARGEMENT DES MÉDICAMENTS DU JOUR
    // Remplacer par :
    //   SELECT COUNT(*) FROM prises WHERE date = today AND pris = 1  → MED_PRIS
    //   SELECT COUNT(*) FROM prises WHERE date = today               → MED_TOTAL
    //   SELECT heure FROM prises WHERE date = today AND pris = 0 ORDER BY heure ASC LIMIT 1
    // ============================================================
    private void loadMedicamentData() {
        try {
            if (tvMedProgress != null) {
                tvMedProgress.setText(MED_PRIS + "/" + MED_TOTAL);
            }
            if (tvNextMedReminder != null) {
                tvNextMedReminder.setText(MED_PROCHAIN_RAPPEL);
            }
        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur loadMedicamentData: " + e.getMessage());
        }
    }

    // ============================================================
    // CHARGEMENT DES ORDONNANCES
    // Remplacer par :
    //   SELECT COUNT(*) FROM ordonnances
    // ============================================================
    private void loadOrdonnanceData() {
        try {
            if (tvOrdoCount != null)  tvOrdoCount.setText(String.valueOf(ORDO_COUNT));
            if (tvOrdoStatus != null) tvOrdoStatus.setText("✓ À jour");
        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur loadOrdonnanceData: " + e.getMessage());
        }
    }

    // ============================================================
    // GESTION DES CLICS
    // ============================================================
    private void setupClickListeners() {

        // Carte RDV → aller à l'onglet Suivi
        if (cardNextRdv != null) {
            cardNextRdv.setOnClickListener(v -> navigateToSuivi());
        }

        // Bouton "Voir détails" dans la carte RDV
        if (btnVoirSuivi != null) {
            btnVoirSuivi.setOnClickListener(v -> navigateToSuivi());
        }

        // Carte Médicaments → aller à l'onglet Suivi
        if (cardMedications != null) {
            cardMedications.setOnClickListener(v -> navigateToSuivi());
        }

        // Carte Ordonnances → aller à l'onglet Suivi
        if (cardOrdonnances != null) {
            cardOrdonnances.setOnClickListener(v -> navigateToSuivi());
        }

        // Bouton Suivi Complet → aller à l'onglet Suivi
        if (btnSuiviComplet != null) {
            btnSuiviComplet.setOnClickListener(v -> navigateToSuivi());
        }
    }

    // ============================================================
    // NAVIGATION VERS L'ONGLET SUIVI
    // Adapte selon ta navigation (BottomNavigationView ou NavController)
    // ============================================================
    private void navigateToSuivi() {
        try {
            if (getView() == null) return;
            androidx.navigation.Navigation
                    .findNavController(getView())
                    .navigate(R.id.fragmentTracking);
        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur navigation: " + e.getMessage());
        }
    }
}