package com.mobileproject.se77a.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mobileproject.se77a.R;

public class FragmentMedications extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Au clic sur le bouton "+", on affiche le dialogue
        view.findViewById(R.id.fab_add_medication).setOnClickListener(v -> showAddMedicationDialog());
    }

    private void showAddMedicationDialog() {
        // Création du BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medication, null);

        // Configuration du menu déroulant (Fréquence)
        String[] frequencies = new String[]{
                "Une fois par jour", 
                "Deux fois par jour", 
                "Trois fois par jour", 
                "Toutes les 8 heures", 
                "Toutes les 12 heures"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, frequencies);
        AutoCompleteTextView actvFrequency = dialogView.findViewById(R.id.actv_frequency);
        if (actvFrequency != null) {
            actvFrequency.setAdapter(adapter);
        }

        // Action bouton Annuler
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        // Action bouton Enregistrer
        dialogView.findViewById(R.id.btn_save_medication).setOnClickListener(v -> {
            // Ici vous pourrez ajouter la logique de sauvegarde dans la base de données Room
            dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.show();
    }
}
