package com.mobileproject.se77a.fragments; // Remplace par ton package réel

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;

public class FragmentTracking extends Fragment {

    private CardView cardAppointment, btnVoirDetails;
    private ProgressBar progressMedication;

    public FragmentTracking() {
        // Constructeur requis
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Chargement du layout blanc
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        // Initialisation des composants
        btnVoirDetails = view.findViewById(R.id.btn_voir_details);
        progressMedication = view.findViewById(R.id.progress_medication);
        cardAppointment = view.findViewById(R.id.card_appointment);

        // Actions au clic
        btnVoirDetails.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détails du rendez-vous", Toast.LENGTH_SHORT).show()
        );

        cardAppointment.setOnClickListener(v ->
                Toast.makeText(getContext(), "Ouverture du calendrier", Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}