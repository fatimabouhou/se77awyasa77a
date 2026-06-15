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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.adapters.AppointmentAdapter;
import com.mobileproject.se77a.database.entities.Appointment;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.repository.AppointmentRepository;
import com.mobileproject.se77a.repository.MedicationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FragmentTracking extends Fragment {

    // ── Section 1 : Rendez-vous (Carrousel Horizontal) ──────────────────────
    private RecyclerView       rvNextAppointments;
    private AppointmentAdapter appointmentAdapter;

    // ── Section 2 : Médicaments ────────────────────────────────────────────
    private LinearLayout cardMorning, cardNoon, cardEvening;
    private ProgressBar  progressMedication;

    private View     viewMorningIndicator, viewNoonIndicator, viewEveningIndicator;
    private TextView tvMorningTime, tvNoonTime, tvEveningTime;

    private MedicationRepository  medicationRepository;
    private AppointmentRepository appointmentRepository;
    private List<Medication>      activeMedicationsList = new ArrayList<>();

    // ── Section 3 : Grille 8 services ─────────────────────────────────────
    private CardView cardAppointment, cardMap, cardPrescription, cardResults;
    private CardView cardMedications, cardCall, cardHistory, cardNotifications;

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

    // ── Initialisation des vues ────────────────────────────────────────────
    private void initViews(View view) {
        // Configuration du Recycler View Horizontal pour les RDV
        rvNextAppointments = view.findViewById(R.id.rv_next_appointments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvNextAppointments.setLayoutManager(layoutManager);

        // Initialisation de l'adapter avec écouteur de clic sur le bouton Détails
        appointmentAdapter = new AppointmentAdapter(appt -> {
            Toast.makeText(getContext(),
                    "📋 " + appt.doctorName + " — " + appt.specialty +
                            "\n📅 " + appt.date + " à " + appt.time +
                            "\n📍 " + appt.address +
                            "\n📞 " + appt.phone,
                    Toast.LENGTH_LONG).show();
        });
        rvNextAppointments.setAdapter(appointmentAdapter);

        // Ajout du SnapHelper pour effet d'aimantation par carte (effet Swiper)
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvNextAppointments);

        // Vues Médicaments
        cardMorning          = view.findViewById(R.id.card_morning);
        cardNoon             = view.findViewById(R.id.card_noon);
        cardEvening          = view.findViewById(R.id.card_evening);
        progressMedication   = view.findViewById(R.id.progress_medication);

        viewMorningIndicator = view.findViewById(R.id.view_morning_indicator);
        viewNoonIndicator    = view.findViewById(R.id.view_noon_indicator);
        viewEveningIndicator = view.findViewById(R.id.view_evening_indicator);

        tvMorningTime = view.findViewById(R.id.tv_morning_time);
        tvNoonTime    = view.findViewById(R.id.tv_noon_time);
        tvEveningTime = view.findViewById(R.id.tv_evening_time);

        // Vues Grille de services
        cardAppointment   = view.findViewById(R.id.card_appointment);
        cardMap           = view.findViewById(R.id.card_map);
        cardPrescription  = view.findViewById(R.id.card_prescription);
        cardResults       = view.findViewById(R.id.card_results);
        cardMedications   = view.findViewById(R.id.card_medications);
        cardCall          = view.findViewById(R.id.card_call);
        cardHistory       = view.findViewById(R.id.card_history);
        cardNotifications = view.findViewById(R.id.card_notifications);
    }

    // ── Chargement des données ─────────────────────────────────────────────
    private void loadData() {
        if (getActivity() == null) return;

        // ── RDV : Récupération de TOUS les prochains rendez-vous ───────────
        appointmentRepository = new AppointmentRepository(getActivity().getApplication());
        appointmentRepository.getAllAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments == null || appointments.isEmpty()) {
                rvNextAppointments.setVisibility(View.GONE);
            } else {
                rvNextAppointments.setVisibility(View.VISIBLE);
                appointmentAdapter.setAppointments(appointments);
            }
        });

        // ── Médicaments ────────────────────────────────────────────────────
        medicationRepository = new MedicationRepository(getActivity().getApplication());
        medicationRepository.resetIfNewDay();
        medicationRepository.getAllMedications().observe(getViewLifecycleOwner(), medications -> {
            if (medications == null) return;
            activeMedicationsList = medications;
            updateMedicationUI(medications);
        });
    }

    // ── Logique UI médicaments ─────────────────────────────────────────────
    private void updateMedicationUI(List<Medication> medications) {
        int totalDoses = 0, takenDoses = 0;
        int matinTotal = 0, matinPris = 0;
        int midiTotal  = 0, midiPris  = 0;
        int soirTotal  = 0, soirPris  = 0;

        List<String> morningPending = new ArrayList<>();
        List<String> noonPending    = new ArrayList<>();
        List<String> eveningPending = new ArrayList<>();

        for (Medication med : medications) {
            if (!med.isActive) continue;

            String[] scheduledTimes = parseTimes(med.reminderTime);
            List<String> takenList  = new ArrayList<>(Arrays.asList(parseTimes(med.takenTimes)));

            for (String time : scheduledTimes) {
                if (time.isEmpty()) continue;

                int hour     = parseHour(time);
                boolean pris = takenList.contains(time);

                totalDoses++;
                if (pris) takenDoses++;

                if (hour < 12) {
                    matinTotal++;
                    if (pris)  matinPris++;
                    else       morningPending.add(time);
                } else if (hour < 18) {
                    midiTotal++;
                    if (pris)  midiPris++;
                    else       noonPending.add(time);
                } else {
                    soirTotal++;
                    if (pris)  soirPris++;
                    else       eveningPending.add(time);
                }
            }
        }

        Collections.sort(morningPending);
        Collections.sort(noonPending);
        Collections.sort(eveningPending);

        String firstMorning = morningPending.isEmpty() ? "" : morningPending.get(0);
        String firstNoon    = noonPending.isEmpty()    ? "" : noonPending.get(0);
        String firstEvening = eveningPending.isEmpty() ? "" : eveningPending.get(0);

        updateSlotUI(viewMorningIndicator, tvMorningTime, cardMorning, matinTotal, matinPris, firstMorning);
        updateSlotUI(viewNoonIndicator, tvNoonTime, cardNoon, midiTotal, midiPris, firstNoon);
        updateSlotUI(viewEveningIndicator, tvEveningTime, cardEvening, soirTotal, soirPris, firstEvening);

        if (progressMedication != null) {
            int percent = (totalDoses == 0) ? 0 : (takenDoses * 100) / totalDoses;
            progressMedication.setProgress(percent);
        }
    }

    private void updateSlotUI(View indicator, TextView timeView, LinearLayout card, int total, int pris, String firstTime) {
        boolean hasDoses = total > 0;
        boolean allTaken = hasDoses && (pris == total);

        if (indicator != null) {
            indicator.setBackgroundResource(allTaken ? R.drawable.bg_med_checked : R.drawable.bg_med_pending);
        }
        if (timeView != null) {
            timeView.setText(!firstTime.isEmpty() ? firstTime : "--:--");
        }
        if (card != null) {
            card.setAlpha(allTaken ? 0.5f : 1.0f);
        }
    }

    // ── Gestionnaire des clics ─────────────────────────────────────────────
    private void setupClickListeners() {
        if (cardAppointment   != null) cardAppointment.setOnClickListener(v -> takeAppointment());
        if (cardMap           != null) cardMap.setOnClickListener(v -> openMaps());
        if (cardPrescription  != null) cardPrescription.setOnClickListener(v -> takePrescriptionPhoto());
        if (cardResults       != null) cardResults.setOnClickListener(v -> takeAnalysisPhoto());
        if (cardMedications   != null) cardMedications.setOnClickListener(v -> medicationReminders());
        if (cardCall          != null) cardCall.setOnClickListener(v -> callCabinet());
        if (cardHistory       != null) cardHistory.setOnClickListener(v -> showHistory());
        if (cardNotifications != null) cardNotifications.setOnClickListener(v -> showNotifications());

        if (cardMorning != null) cardMorning.setOnClickListener(v -> markMedicationTaken("Matin"));
        if (cardNoon    != null) cardNoon.setOnClickListener(v -> markMedicationTaken("Midi"));
        if (cardEvening != null) cardEvening.setOnClickListener(v -> markMedicationTaken("Soir"));
    }

    // ── Marquage dose par dose ─────────────────────────────────────────────
    private void markMedicationTaken(String slot) {
        if (medicationRepository == null) return;
        boolean doseTrouvee = false;

        for (Medication med : activeMedicationsList) {
            if (!med.isActive) continue;

            String[] scheduledTimes = parseTimes(med.reminderTime);
            List<String> takenList  = new ArrayList<>(Arrays.asList(parseTimes(med.takenTimes)));

            for (String time : scheduledTimes) {
                if (time.isEmpty()) continue;

                int hour = parseHour(time);
                boolean correspondAuSlot =
                        (slot.equals("Matin") && hour < 12) ||
                                (slot.equals("Midi")  && hour >= 12 && hour < 18) ||
                                (slot.equals("Soir")  && hour >= 18);

                if (correspondAuSlot && !takenList.contains(time)) {
                    takenList.add(time);
                    String newTakenTimes = String.join(",", takenList);
                    boolean toutPris = takenList.containsAll(Arrays.asList(scheduledTimes));

                    medicationRepository.updateTakenTimes(med.id, newTakenTimes, toutPris);
                    Toast.makeText(getContext(), med.name + " · " + time + " ✓", Toast.LENGTH_SHORT).show();
                    doseTrouvee = true;
                    break;
                }
            }
            if (doseTrouvee) break;
        }

        if (!doseTrouvee) {
            Toast.makeText(getContext(), "Toutes les doses du " + slot + " sont déjà prises ✓", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Utilitaires ────────────────────────────────────────────────────────
    private String[] parseTimes(String raw) {
        if (raw == null || raw.trim().isEmpty()) return new String[0];
        String[] parts = raw.split(",");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }

    private int parseHour(String time) {
        try {
            return Integer.parseInt(time.split(":")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ── 8 services actions ──────────────────────────────────────────────────
    private void takeAppointment() {
        if (appointmentRepository != null) {
            AppointmentBottomSheet sheet = new AppointmentBottomSheet(appointmentRepository);
            sheet.show(getParentFragmentManager(), "appointment");
        }
    }

    private void openMaps() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new FragmentMap())
                    .addToBackStack(null)
                    .commit();
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
        Toast.makeText(getContext(), "💊 Paramètres des rappels médicaments", Toast.LENGTH_LONG).show();
    }

    private void callCabinet() {
        if (appointmentRepository == null) return;
        appointmentRepository.getAllAppointments().observe(getViewLifecycleOwner(), appointments -> {
            String phone = (appointments != null && !appointments.isEmpty() && appointments.get(0).phone != null)
                    ? appointments.get(0).phone : "0512345678";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        });
    }

    private void showHistory() {
        Toast.makeText(getContext(),
                "📜 Historique des consultations:\n\n• 10/05/2026 - Dr. Benali (Cardio)\n• 25/04/2026 - Dr. Fathi (Généraliste)",
                Toast.LENGTH_LONG).show();
    }

    private void showNotifications() {
        Toast.makeText(getContext(), "🔔 Notifications activées\nRappel: Prendre vos médicaments dans 1h", Toast.LENGTH_SHORT).show();
    }
}