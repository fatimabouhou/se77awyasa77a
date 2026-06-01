package com.mobileproject.se77a.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FragmentHome extends Fragment {

    // ============================================================
    // VUES
    // ============================================================
    private TextView tvUsername, tvDate, tvAvatarInitials;

    // Résumé du jour
    private TextView tvMedProgress, tvRdvCount, tvOrdoCount;

    // Alertes
    private TextView tvAlerteMedDetail, tvAlerteMedHeure;
    private TextView tvAlerteRdvDoctor, tvAlerteRdvSpecialty, tvAlerteRdvCountdown;

    // Conseil
    private TextView tvConseilTitre, tvConseilTexte;

    // Handler pour changement automatique du conseil
    private final Handler conseilHandler = new Handler(Looper.getMainLooper());
    private int conseilIndex = 0;
    private static final long CONSEIL_INTERVAL_MS = 60 * 60 * 1000; // 1 heure

    // ============================================================
    // DONNÉES STATIQUES (à remplacer par SQLite/API plus tard)
    // ============================================================

    // Médicaments
    private static final int    MED_PRIS            = 2;
    private static final int    MED_TOTAL           = 3;
    private static final String MED_PROCHAIN_RAPPEL = "Prochain rappel : Soir 20h00";
    private static final String MED_HEURE           = "20:00";

    // RDV
    private static final String RDV_DOCTOR    = "Dr. Ahmed Benali";
    private static final String RDV_SPECIALTY = "Cardiologue";
    private static final int    RDV_CE_MOIS   = 1;
    private static final long   RDV_TIMESTAMP = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(18);

    // Ordonnances
    private static final int ORDO_COUNT = 3;

    // ============================================================
    // CONSEILS SANTÉ (14 conseils, change chaque heure)
    // ============================================================
    private static final String[][] CONSEILS = {
            {
                    "Hydratation",
                    "Buvez au moins 1,5L d'eau par jour. En été ou après un effort, augmentez à 2L."
            },
            {
                    "Alimentation équilibrée",
                    "Consommez 5 fruits et légumes par jour. Ils apportent vitamines, fibres et antioxydants essentiels."
            },
            {
                    "Activité physique",
                    "30 minutes de marche rapide par jour réduisent les risques cardiovasculaires de 35%."
            },
            {
                    "Sommeil réparateur",
                    "Dormez entre 7 et 8 heures par nuit. Un bon sommeil renforce votre système immunitaire."
            },
            {
                    "Gestion du stress",
                    "Pratiquez 5 minutes de respiration profonde le matin pour réduire l'anxiété et le stress."
            },
            {
                    "Évitez le tabac",
                    "Arrêter de fumer réduit le risque de maladies cardiaques dès les premières semaines."
            },
            {
                    "Limitez le café",
                    "Ne dépassez pas 3 tasses de café par jour. Préférez les tisanes le soir pour mieux dormir."
            },
            {
                    "Hygiène bucco-dentaire",
                    "Brossez vos dents 2 fois par jour pendant 2 minutes. Utilisez du fil dentaire quotidiennement."
            },
            {
                    "Pause écran",
                    "Faites une pause de 20 secondes toutes les 20 minutes d'écran pour reposer vos yeux."
            },
            {
                    "Protection solaire",
                    "Appliquez une crème solaire SPF 30+ avant de sortir, même par temps nuageux."
            },
            {
                    "Petit-déjeuner",
                    "Ne sautez pas le petit-déjeuner. C'est le repas le plus important pour maintenir votre énergie."
            },
            {
                    "Évitez la sédentarité",
                    "Levez-vous et marchez 5 minutes toutes les heures si vous travaillez assis."
            },
            {
                    "Hygiène des mains",
                    "Lavez-vous les mains 30 secondes avec du savon avant chaque repas et après les toilettes."
            },
            {
                    "Suivi médical",
                    "Consultez votre médecin au moins une fois par an pour un bilan de santé préventif."
            }
    };

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
        loadResumeData();
        loadAlertesData();
        startConseilRotation();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Arrêter le handler pour éviter les fuites mémoire
        conseilHandler.removeCallbacksAndMessages(null);
    }

    // ============================================================
    // INITIALISATION DES VUES
    // ============================================================
    private void initViews(View view) {
        tvUsername       = view.findViewById(R.id.tv_username);
        tvDate           = view.findViewById(R.id.tv_date);
        tvAvatarInitials = view.findViewById(R.id.tv_avatar_initials);

        tvMedProgress    = view.findViewById(R.id.tv_med_progress);
        tvRdvCount       = view.findViewById(R.id.tv_rdv_count);
        tvOrdoCount      = view.findViewById(R.id.tv_ordo_count);

        tvAlerteMedDetail    = view.findViewById(R.id.tv_alerte_med_detail);
        tvAlerteMedHeure     = view.findViewById(R.id.tv_alerte_med_heure);
        tvAlerteRdvDoctor    = view.findViewById(R.id.tv_alerte_rdv_doctor);
        tvAlerteRdvSpecialty = view.findViewById(R.id.tv_alerte_rdv_specialty);
        tvAlerteRdvCountdown = view.findViewById(R.id.tv_alerte_rdv_countdown);

        tvConseilTitre  = view.findViewById(R.id.tv_conseil_titre);
        tvConseilTexte  = view.findViewById(R.id.tv_conseil_texte);
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES UTILISATEUR
    // ============================================================
    private void loadUserData() {
        if (getContext() == null) return;
        try {
            SharedPreferences prefs = getContext()
                    .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String fullName = prefs.getString("current_user_name", "Utilisateur");

            if (tvUsername != null) tvUsername.setText(fullName);

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

            String today = new SimpleDateFormat("EEEE d MMMM", Locale.FRENCH).format(new Date());
            String dateFormatted = today.substring(0, 1).toUpperCase() + today.substring(1);
            if (tvDate != null) tvDate.setText(dateFormatted);

        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur loadUserData: " + e.getMessage());
        }
    }

    // ============================================================
    // RÉSUMÉ DU JOUR
    // Remplacer les constantes par des requêtes SQLite :
    //   SELECT COUNT(*) FROM prises WHERE date = today AND pris = 1  → MED_PRIS
    //   SELECT COUNT(*) FROM prises WHERE date = today               → MED_TOTAL
    //   SELECT COUNT(*) FROM rendez_vous WHERE strftime('%m',date) = strftime('%m','now') → RDV_CE_MOIS
    //   SELECT COUNT(*) FROM ordonnances                             → ORDO_COUNT
    // ============================================================
    private void loadResumeData() {
        try {
            if (tvMedProgress != null)
                tvMedProgress.setText(MED_PRIS + "/" + MED_TOTAL);
            if (tvRdvCount != null)
                tvRdvCount.setText(String.valueOf(RDV_CE_MOIS));
            if (tvOrdoCount != null)
                tvOrdoCount.setText(String.valueOf(ORDO_COUNT));
        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur loadResumeData: " + e.getMessage());
        }
    }

    // ============================================================
    // ALERTES DU JOUR
    // Remplacer par :
    //   SELECT heure FROM prises WHERE date = today AND pris = 0 ORDER BY heure ASC LIMIT 1
    //   SELECT * FROM rendez_vous WHERE date >= NOW() ORDER BY date ASC LIMIT 1
    // ============================================================
    private void loadAlertesData() {
        try {
            if (tvAlerteMedDetail != null) tvAlerteMedDetail.setText(MED_PROCHAIN_RAPPEL);
            if (tvAlerteMedHeure  != null) tvAlerteMedHeure.setText(MED_HEURE);
            if (tvAlerteRdvDoctor != null) tvAlerteRdvDoctor.setText(RDV_DOCTOR);
            if (tvAlerteRdvSpecialty != null) tvAlerteRdvSpecialty.setText(RDV_SPECIALTY);

            long diffMs    = RDV_TIMESTAMP - System.currentTimeMillis();
            long diffHours = TimeUnit.MILLISECONDS.toHours(diffMs);
            long diffDays  = TimeUnit.MILLISECONDS.toDays(diffMs);

            String countdown;
            if (diffMs < 0) {
                countdown = "Passé";
            } else if (diffHours < 24) {
                countdown = "dans " + diffHours + "h";
            } else {
                countdown = "dans " + diffDays + " j";
            }
            if (tvAlerteRdvCountdown != null) tvAlerteRdvCountdown.setText(countdown);

        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur loadAlertesData: " + e.getMessage());
        }
    }

    // ============================================================
    // CONSEIL SANTÉ — ROTATION AUTOMATIQUE TOUTES LES HEURES
    // L'index de départ est basé sur l'heure actuelle pour que
    // tous les utilisateurs voient le même conseil au même moment.
    // ============================================================
    private void startConseilRotation() {
        // Index initial basé sur l'heure actuelle
        conseilIndex = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % CONSEILS.length;
        afficherConseil(conseilIndex);

        // Calcul du délai avant la prochaine heure pile
        Calendar now = Calendar.getInstance();
        long millisUntilNextHour =
                TimeUnit.MINUTES.toMillis(60 - now.get(Calendar.MINUTE))
                        - TimeUnit.SECONDS.toMillis(now.get(Calendar.SECOND));

        // Premier changement à la prochaine heure pile, puis toutes les heures
        conseilHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                conseilIndex = (conseilIndex + 1) % CONSEILS.length;
                afficherConseil(conseilIndex);
                conseilHandler.postDelayed(this, CONSEIL_INTERVAL_MS);
            }
        }, millisUntilNextHour);
    }

    private void afficherConseil(int index) {
        try {
            if (tvConseilTitre != null) tvConseilTitre.setText(CONSEILS[index][0]);
            if (tvConseilTexte != null) tvConseilTexte.setText(CONSEILS[index][1]);
        } catch (Exception e) {
            android.util.Log.e("FragmentHome", "Erreur afficherConseil: " + e.getMessage());
        }
    }
}