package com.mobileproject.se77a.fragments;

import android.os.Bundle;
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

public class FragmentHome extends Fragment {

    private TextView tvUsername, tvHealthScore, tvRdvCount, tvMedCount, tvOrdoCount;
    private CardView btnNotification;
    private LinearLayout cardStatRdv, cardStatMed, cardStatOrdo;
    private ProgressBar progressHealthScore;

    public FragmentHome() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Liaison des vues de textes
        tvUsername = view.findViewById(R.id.tv_username);
        tvHealthScore = view.findViewById(R.id.tv_health_score);
        tvRdvCount = view.findViewById(R.id.tv_rdv_count);
        tvMedCount = view.findViewById(R.id.tv_med_count);
        tvOrdoCount = view.findViewById(R.id.tv_ordo_count);

        // Liaison de l'anneau de progression de santé
        progressHealthScore = view.findViewById(R.id.progress_health_score);

        // Liaison des boutons et layouts interactifs
        btnNotification = view.findViewById(R.id.btn_notification);
        cardStatRdv = view.findViewById(R.id.card_stat_rdv);
        cardStatMed = view.findViewById(R.id.card_stat_med);
        cardStatOrdo = view.findViewById(R.id.card_stat_ordo);

        // Attribution statique ou dynamique du score dans la jauge (Ex: 85%)
        if (progressHealthScore != null) {
            progressHealthScore.setProgress(85);
        }

        btnNotification.setOnClickListener(v -> Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show());

        // Exemple d'actions sur vos statistiques
        cardStatRdv.setOnClickListener(v -> Toast.makeText(getContext(), "Détails RDV", Toast.LENGTH_SHORT).show());
        cardStatMed.setOnClickListener(v -> Toast.makeText(getContext(), "Détails Médicaments", Toast.LENGTH_SHORT).show());
        cardStatOrdo.setOnClickListener(v -> Toast.makeText(getContext(), "Détails Ordonnances", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Injection propre du fragment graphique lissé dans son conteneur dédié
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.chart_container, new FragmentVisitsChart())
                    .commit();
        }
    }
}