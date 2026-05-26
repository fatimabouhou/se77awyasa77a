package com.mobileproject.se77a.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentHome extends Fragment {

    private TextView tvUsername, tvDate, tvHealthScore;
    private TextView tvSleepHours, tvStepsCount, tvWaterCount;
    private TextView tvRdvCount, tvMedCount, tvOrdoCount;
    private LinearLayout tvStatusBadge;  // ← C'est un LinearLayout, pas un TextView !
    private CardView btnNotification;
    private LinearLayout cardStatRdv, cardStatMed, cardStatOrdo;
    private LinearLayout cardSleep, cardSteps, cardWater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Header
        tvUsername = view.findViewById(R.id.tv_username);
        tvDate = view.findViewById(R.id.tv_date);
        btnNotification = view.findViewById(R.id.btn_notification);

        // Carte santé dashboard
        tvHealthScore = view.findViewById(R.id.tv_health_score);
        tvStatusBadge = view.findViewById(R.id.tv_status_badge);  // ← LinearLayout
        tvSleepHours = view.findViewById(R.id.tv_sleep_hours);
        tvStepsCount = view.findViewById(R.id.tv_steps_count);
        tvWaterCount = view.findViewById(R.id.tv_water_count);

        // Cartes stats résumé
        cardStatRdv = view.findViewById(R.id.card_stat_rdv);
        cardStatMed = view.findViewById(R.id.card_stat_med);
        cardStatOrdo = view.findViewById(R.id.card_stat_ordo);

        // Les TextView à l'intérieur des cartes
        tvRdvCount = view.findViewById(R.id.tv_rdv_count);
        tvMedCount = view.findViewById(R.id.tv_med_count);
        tvOrdoCount = view.findViewById(R.id.tv_ordo_count);

        // Métriques cliquables
        cardSleep = view.findViewById(R.id.card_sleep);
        cardSteps = view.findViewById(R.id.card_steps);
        cardWater = view.findViewById(R.id.card_water);

        // Chargement des données
        loadUserData();

        // Gestion des clics
        setupClickListeners();

        return view;
    }

    private void loadUserData() {
        try {
            tvUsername.setText("Fatima Bouhou");

            // Date dynamique
            String today = new SimpleDateFormat("EEEE d MMMM", Locale.FRENCH).format(new Date());
            String dateFormatted = today.substring(0, 1).toUpperCase() + today.substring(1);
            tvDate.setText(dateFormatted);

            tvHealthScore.setText("85");
            tvSleepHours.setText("7h24");
            tvStepsCount.setText("6 542");
            tvWaterCount.setText("4/8");
            tvRdvCount.setText("2");
            tvMedCount.setText("3");
            tvOrdoCount.setText("5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        btnNotification.setOnClickListener(v ->
                Toast.makeText(getContext(), "Profil", Toast.LENGTH_SHORT).show());

        cardStatRdv.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détails Rendez-vous", Toast.LENGTH_SHORT).show());

        cardStatMed.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détails Médicaments", Toast.LENGTH_SHORT).show());

        cardStatOrdo.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détails Ordonnances", Toast.LENGTH_SHORT).show());

        cardSleep.setOnClickListener(v ->
                Toast.makeText(getContext(), "Analyse du sommeil", Toast.LENGTH_SHORT).show());

        cardSteps.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détail des pas", Toast.LENGTH_SHORT).show());

        cardWater.setOnClickListener(v ->
                Toast.makeText(getContext(), "Suivi hydratation", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            try {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.chart_container, new FragmentVisitsChart())
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}