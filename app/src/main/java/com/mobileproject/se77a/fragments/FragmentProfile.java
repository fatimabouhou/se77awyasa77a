package com.mobileproject.se77a.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.AppDatabase;

public class FragmentProfile extends Fragment {

    // CORRECTION : On utilise exactement le même fichier que ton LoginActivity
    private static final String PREFS_NAME = "user_prefs";

    private TextView tvAvatar, tvNom, tvEmail, tvAge, tvGroupeSanguin, tvMorphologie;
    private TextView tvStatConsultations, tvStatMedicaments, tvStatOrdonnances;
    private TextView tvMedecin, tvAllergies, tvAntecedents;
    private RelativeLayout itemRappels, itemOrdonnances, itemConfidentialite, itemDeconnexion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        chargerProfil();
        chargerStats();
        configurerNavigation(view);
    }

    private void bindViews(View view) {
        tvAvatar            = view.findViewById(R.id.tvAvatar);
        tvNom               = view.findViewById(R.id.tvNom);
        tvEmail             = view.findViewById(R.id.tvEmail);
        tvAge               = view.findViewById(R.id.tvAge);
        tvGroupeSanguin     = view.findViewById(R.id.tvGroupeSanguin);
        tvMorphologie       = view.findViewById(R.id.tvMorphologie);

        tvStatConsultations = view.findViewById(R.id.tvStatConsultations);
        tvStatMedicaments   = view.findViewById(R.id.tvStatMedicaments);
        tvStatOrdonnances   = view.findViewById(R.id.tvStatOrdonnances);

        tvMedecin           = view.findViewById(R.id.tvMedecin);
        tvAllergies         = view.findViewById(R.id.tvAllergies);
        tvAntecedents       = view.findViewById(R.id.tvAntecedents);

        itemRappels         = view.findViewById(R.id.itemRappels);
        itemOrdonnances     = view.findViewById(R.id.itemOrdonnances);
        itemConfidentialite = view.findViewById(R.id.itemConfidentialite);
        itemDeconnexion     = view.findViewById(R.id.itemDeconnexion);
    }

    private void chargerProfil() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // CORRECTION : On récupère "current_user_name" au lieu de "nom"
        String nom           = prefs.getString("current_user_name", "Utilisateur");
        String email         = prefs.getString("email", "—");
        String age           = prefs.getString("age", "—");
        String groupeSanguin = prefs.getString("groupe_sanguin", "—");
        String taille        = prefs.getString("taille", "—");
        String poids         = prefs.getString("poids", "—");
        String medecin       = prefs.getString("medecin", "—");
        String allergies     = prefs.getString("allergies", "—");
        String antecedents   = prefs.getString("antecedents", "—");

        tvAvatar.setText(extraireInitiales(nom));
        tvNom.setText(nom);
        tvEmail.setText(email);

        tvAge.setText(age.equals("—") ? age : age + " ans");
        tvGroupeSanguin.setText(groupeSanguin.equals("—") ? groupeSanguin : "Groupe " + groupeSanguin);
        tvMorphologie.setText(taille.equals("—") || poids.equals("—") ? "—" : taille + " cm · " + poids + " kg");

        tvMedecin.setText(medecin);
        tvAllergies.setText(allergies);
        tvAntecedents.setText(antecedents);
    }

    private String extraireInitiales(String nom) {
        if (nom == null || nom.trim().isEmpty()) return "?";
        String[] parts = nom.trim().split("\\s+");
        if (parts.length == 1) return String.valueOf(parts[0].charAt(0)).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0))).toUpperCase();
    }

    private void chargerStats() {
        AppDatabase db = AppDatabase.getInstance(requireContext());

        int nbConsultations = db.appointmentDao().countAppointments();
        int nbMedicaments   = db.medicationDao().countMedications();
        int nbOrdonnances   = db.appointmentDao().countOrdonnances();

        tvStatConsultations.setText(String.valueOf(nbConsultations));
        tvStatMedicaments.setText(String.valueOf(nbMedicaments));
        tvStatOrdonnances.setText(String.valueOf(nbOrdonnances));
    }

    private void configurerNavigation(View view) {
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Paramètres", Toast.LENGTH_SHORT).show();
        });

        itemRappels.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Rappels & Notifications", Toast.LENGTH_SHORT).show();
        });

        itemOrdonnances.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Mes Ordonnances", Toast.LENGTH_SHORT).show();
        });

        itemConfidentialite.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Confidentialité", Toast.LENGTH_SHORT).show();
        });

        itemDeconnexion.setOnClickListener(this::deconnecter);
    }

    private void deconnecter(View v) {
        requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        try {
            NavController nav = Navigation.findNavController(v);
            Toast.makeText(requireContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), "Erreur NavGraph", Toast.LENGTH_SHORT).show();
        }
    }

    // CORRECTION : La méthode de sauvegarde utilise aussi la clé globale "current_user_name"
    public static void sauvegarderProfil(Context ctx,
                                         String nom, String email,
                                         String age, String groupeSanguin,
                                         String taille, String poids,
                                         String medecin, String allergies,
                                         String antecedents) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString("current_user_name", nom)
                .putString("email", email)
                .putString("age", age)
                .putString("groupe_sanguin", groupeSanguin)
                .putString("taille", taille)
                .putString("poids", poids)
                .putString("medecin", medecin)
                .putString("allergies", allergies)
                .putString("antecedents", antecedents)
                .apply();
    }
}