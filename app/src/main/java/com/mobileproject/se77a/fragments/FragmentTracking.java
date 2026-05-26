package com.mobileproject.se77a.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;

public class FragmentTracking extends Fragment {

    // ============================================================
    // SECTION 1 : RENDEZ-VOUS
    // ============================================================
    private TextView tvNextDoctor, tvNextSpecialty, tvNextDate, tvCountdown;
    private CardView btnViewDetails;

    // ============================================================
    // SECTION 2 : MÉDICAMENTS
    // ============================================================
    private LinearLayout cardMorning, cardNoon, cardEvening;
    private ProgressBar progressMedication;

    // ============================================================
    // SECTION 3 : GRILLE FONCTIONNALITÉS (8 fonctionnalités)
    // ============================================================
    private CardView cardAppointment;      // Prendre rendez-vous
    private CardView cardMap;              // Géolocalisation cabinets/pharmacies
    private CardView cardPrescription;     // Photo ordonnances
    private CardView cardResults;          // Photo résultats d'analyses
    private CardView cardMedications;      // Rappels médicaments
    private CardView cardCall;             // Appel direct au cabinet
    private CardView cardHistory;          // Historique consultations
    private CardView cardNotifications;    // Notifications

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        initViews(view);
        loadData();
        setupClickListeners();

        return view;
    }

    // ============================================================
    // INITIALISATION DES VUES
    // ============================================================
    private void initViews(View view) {
        // Rendez-vous
        tvNextDoctor = view.findViewById(R.id.tv_next_doctor);
        tvNextSpecialty = view.findViewById(R.id.tv_next_specialty);
        tvNextDate = view.findViewById(R.id.tv_next_date);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        btnViewDetails = view.findViewById(R.id.btn_voir_details);

        // Médicaments
        cardMorning = view.findViewById(R.id.card_morning);
        cardNoon = view.findViewById(R.id.card_noon);
        cardEvening = view.findViewById(R.id.card_evening);
        progressMedication = view.findViewById(R.id.progress_medication);

        // Grille 8 fonctionnalités
        cardAppointment = view.findViewById(R.id.card_appointment);
        cardMap = view.findViewById(R.id.card_map);
        cardPrescription = view.findViewById(R.id.card_prescription);
        cardResults = view.findViewById(R.id.card_results);
        cardMedications = view.findViewById(R.id.card_medications);
        cardCall = view.findViewById(R.id.card_call);
        cardHistory = view.findViewById(R.id.card_history);
        cardNotifications = view.findViewById(R.id.card_notifications);
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES STATIQUES
    // ============================================================
    private void loadData() {
        // Données du prochain rendez-vous
        if (tvNextDoctor != null) tvNextDoctor.setText("Dr. Ahmed Benali");
        if (tvNextSpecialty != null) tvNextSpecialty.setText("Cardiologue");
        if (tvNextDate != null) tvNextDate.setText("Demain · 14h30");
        if (tvCountdown != null) tvCountdown.setText("dans 18h");

        // Progression médicaments (2/3 pris = 66%)
        if (progressMedication != null) {
            progressMedication.setProgress(66);
        }

        // Style visuel : opacité réduite pour le matin et midi (déjà pris)
        if (cardMorning != null) cardMorning.setAlpha(0.6f);
        if (cardNoon != null) cardNoon.setAlpha(0.6f);
        if (cardEvening != null) cardEvening.setAlpha(1.0f);
    }

    // ============================================================
    // GESTION DES CLICS
    // ============================================================
    private void setupClickListeners() {
        // 1. Détails du rendez-vous
        if (btnViewDetails != null) {
            btnViewDetails.setOnClickListener(v -> showAppointmentDetails());
        }

        // 2. Prendre un rendez-vous
        if (cardAppointment != null) {
            cardAppointment.setOnClickListener(v -> takeAppointment());
        }

        // 3. Géolocalisation (Google Maps)
        if (cardMap != null) {
            cardMap.setOnClickListener(v -> openMaps());
        }

        // 4. Photo ordonnance
        if (cardPrescription != null) {
            cardPrescription.setOnClickListener(v -> takePrescriptionPhoto());
        }

        // 5. Photo résultats d'analyses
        if (cardResults != null) {
            cardResults.setOnClickListener(v -> takeAnalysisPhoto());
        }

        // 6. Rappels médicaments
        if (cardMedications != null) {
            cardMedications.setOnClickListener(v -> medicationReminders());
        }

        // 7. Appel direct au cabinet
        if (cardCall != null) {
            cardCall.setOnClickListener(v -> callCabinet());
        }

        // 8. Historique des consultations
        if (cardHistory != null) {
            cardHistory.setOnClickListener(v -> showHistory());
        }

        // 9. Notifications
        if (cardNotifications != null) {
            cardNotifications.setOnClickListener(v -> showNotifications());
        }

        // Clics sur les plages horaires de médicaments
        if (cardMorning != null) {
            cardMorning.setOnClickListener(v -> markMedicationTaken("Matin"));
        }
        if (cardNoon != null) {
            cardNoon.setOnClickListener(v -> markMedicationTaken("Midi"));
        }
        if (cardEvening != null) {
            cardEvening.setOnClickListener(v -> markMedicationTaken("Soir"));
        }
    }

    // ============================================================
    // IMPLÉMENTATION DES FONCTIONNALITÉS
    // ============================================================

    private void showAppointmentDetails() {
        Toast.makeText(getContext(), "📋 Dr. Ahmed Benali - Cardiologue\n📅 Demain 14h30\n📍 Cabinet Médical Saint-Roch", Toast.LENGTH_LONG).show();
    }

    private void takeAppointment() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(android.provider.CalendarContract.Events.CONTENT_URI);
        intent.putExtra(android.provider.CalendarContract.Events.TITLE, "Consultation médicale");
        intent.putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 86400000);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "📅 Ouvrez l'application Calendrier", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMaps() {
        Uri uri = Uri.parse("geo:0,0?q=pharmacie+de+garde");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "🗺️ Installez Google Maps", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePrescriptionPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "📸 Aucune application appareil photo trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    private void takeAnalysisPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
            Toast.makeText(getContext(), "📸 Prenez en photo vos résultats d'analyses", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "📸 Aucune application appareil photo trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    private void medicationReminders() {
        Toast.makeText(getContext(), "💊 Paramètres des rappels médicaments\nMatin: 08h00 | Midi: 12h00 | Soir: 20h00", Toast.LENGTH_LONG).show();
    }

    private void callCabinet() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0512345678"));
        startActivity(intent);
    }

    private void showHistory() {
        Toast.makeText(getContext(), "📜 Historique des consultations:\n\n• 10/05/2026 - Dr. Benali (Cardio)\n• 25/04/2026 - Dr. Fathi (Généraliste)", Toast.LENGTH_LONG).show();
    }

    private void showNotifications() {
        Toast.makeText(getContext(), "🔔 Notifications activées\nRappel: Prendre vos médicaments dans 1h", Toast.LENGTH_SHORT).show();
    }

    private void markMedicationTaken(String time) {
        Toast.makeText(getContext(), "💊 Médicament du " + time + " marqué comme pris", Toast.LENGTH_SHORT).show();
        if (progressMedication != null) {
            int currentProgress = progressMedication.getProgress();
            if (currentProgress < 100) {
                progressMedication.setProgress(currentProgress + 34); // Passage à 34 pour cumuler proprement jusqu'à 100
            }
        }
    }
}