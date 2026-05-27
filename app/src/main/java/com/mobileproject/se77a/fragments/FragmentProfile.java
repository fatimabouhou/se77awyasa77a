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

/**
 * FragmentProfile
 * Affiche le profil utilisateur : avatar, infos médicales, stats SQLite, menu de navigation.
 *
 * Données persistées dans SharedPreferences (clé "profil_prefs") :
 *   - nom, email, age, groupe_sanguin, taille, poids
 *   - medecin, allergies, antecedents
 *
 * Stats (consultations, medicaments, ordonnances) : à brancher sur ton DAO SQLite.
 *
 * Navigation : remplace les R.id.action_* par tes vraies actions du nav_graph.
 */
public class FragmentProfile extends Fragment {

    // ──────────────────────────────────────────────
    //  Clé SharedPreferences
    // ──────────────────────────────────────────────
    private static final String PREFS_NAME = "profil_prefs";

    // ──────────────────────────────────────────────
    //  Vues
    // ──────────────────────────────────────────────
    private TextView tvAvatar;
    private TextView tvNom;
    private TextView tvEmail;
    private TextView tvAge;
    private TextView tvGroupeSanguin;
    private TextView tvMorphologie;

    private TextView tvStatConsultations;
    private TextView tvStatMedicaments;
    private TextView tvStatOrdonnances;

    private TextView tvMedecin;
    private TextView tvAllergies;
    private TextView tvAntecedents;

    private RelativeLayout itemRappels;
    private RelativeLayout itemOrdonnances;
    private RelativeLayout itemConfidentialite;
    private RelativeLayout itemDeconnexion;

    // ──────────────────────────────────────────────
    //  Lifecycle
    // ──────────────────────────────────────────────

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

    // ──────────────────────────────────────────────
    //  Bind des vues
    // ──────────────────────────────────────────────
    private void bindViews(View view) {
        tvAvatar           = view.findViewById(R.id.tvAvatar);
        tvNom              = view.findViewById(R.id.tvNom);
        tvEmail            = view.findViewById(R.id.tvEmail);
        tvAge              = view.findViewById(R.id.tvAge);
        tvGroupeSanguin    = view.findViewById(R.id.tvGroupeSanguin);
        tvMorphologie      = view.findViewById(R.id.tvMorphologie);

        tvStatConsultations = view.findViewById(R.id.tvStatConsultations);
        tvStatMedicaments   = view.findViewById(R.id.tvStatMedicaments);
        tvStatOrdonnances   = view.findViewById(R.id.tvStatOrdonnances);

        tvMedecin          = view.findViewById(R.id.tvMedecin);
        tvAllergies        = view.findViewById(R.id.tvAllergies);
        tvAntecedents      = view.findViewById(R.id.tvAntecedents);

        itemRappels         = view.findViewById(R.id.itemRappels);
        itemOrdonnances     = view.findViewById(R.id.itemOrdonnances);
        itemConfidentialite = view.findViewById(R.id.itemConfidentialite);
        itemDeconnexion     = view.findViewById(R.id.itemDeconnexion);
    }

    // ──────────────────────────────────────────────
    //  Chargement du profil depuis SharedPreferences
    // ──────────────────────────────────────────────
    private void chargerProfil() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String nom           = prefs.getString("nom", "Utilisateur");
        String email         = prefs.getString("email", "—");
        String age           = prefs.getString("age", "—");
        String groupeSanguin = prefs.getString("groupe_sanguin", "—");
        String taille        = prefs.getString("taille", "—");
        String poids         = prefs.getString("poids", "—");
        String medecin       = prefs.getString("medecin", "—");
        String allergies     = prefs.getString("allergies", "—");
        String antecedents   = prefs.getString("antecedents", "—");

        // Avatar : deux premières initiales du nom
        tvAvatar.setText(extraireInitiales(nom));
        tvNom.setText(nom);
        tvEmail.setText(email);
        tvAge.setText(age + " ans");
        tvGroupeSanguin.setText("Groupe " + groupeSanguin);
        tvMorphologie.setText(taille + " cm · " + poids + " kg");

        tvMedecin.setText(medecin);
        tvAllergies.setText(allergies);
        tvAntecedents.setText(antecedents);
    }

    /**
     * Extrait les deux premières initiales du nom complet.
     * "Ahmed Mansouri" → "AM"
     */
    private String extraireInitiales(String nom) {
        if (nom == null || nom.isEmpty()) return "?";
        String[] parts = nom.trim().split("\\s+");
        if (parts.length == 1) return String.valueOf(parts[0].charAt(0)).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0))).toUpperCase();
    }

    // ──────────────────────────────────────────────
    //  Chargement des stats
    //  → Remplace les valeurs fictives par tes requêtes DAO
    // ──────────────────────────────────────────────
    private void chargerStats() {
        // ── Exemple avec valeurs fictives ──────────
        // Remplace par : int count = monDao.countConsultations();
        int nbConsultations = 12;
        int nbMedicaments   = 3;
        int nbOrdonnances   = 5;

        // ── Avec DAO Room (exemple) ──────────────────
        // AppDatabase db = AppDatabase.getInstance(requireContext());
        // nbConsultations = db.consultationDao().count();
        // nbMedicaments   = db.medicamentDao().countActifs();
        // nbOrdonnances   = db.ordonnanceDao().count();

        tvStatConsultations.setText(String.valueOf(nbConsultations));
        tvStatMedicaments.setText(String.valueOf(nbMedicaments));
        tvStatOrdonnances.setText(String.valueOf(nbOrdonnances));
    }

    // ──────────────────────────────────────────────
    //  Navigation via NavController
    // ──────────────────────────────────────────────
    private void configurerNavigation(View view) {

        // Bouton Paramètres (header)
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> {
            // Si tu as un fragment settings :
            // Navigation.findNavController(v).navigate(R.id.action_profile_to_settings);
            Toast.makeText(requireContext(), "Paramètres", Toast.LENGTH_SHORT).show();
        });

        // Rappels & notifications
        itemRappels.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(v);
            // nav.navigate(R.id.action_profile_to_rappels);
            Toast.makeText(requireContext(), "Rappels", Toast.LENGTH_SHORT).show();
        });

        // Ordonnances
        itemOrdonnances.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(v);
            // nav.navigate(R.id.action_profile_to_ordonnances);
            Toast.makeText(requireContext(), "Ordonnances", Toast.LENGTH_SHORT).show();
        });

        // Confidentialité
        itemConfidentialite.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(v);
            // nav.navigate(R.id.action_profile_to_confidentialite);
            Toast.makeText(requireContext(), "Confidentialité", Toast.LENGTH_SHORT).show();
        });

        // Déconnexion
        itemDeconnexion.setOnClickListener(v -> deconnecter(v));
    }

    // ──────────────────────────────────────────────
    //  Déconnexion
    // ──────────────────────────────────────────────
    private void deconnecter(View v) {
        // 1) Vider les préférences
        requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // 2) Naviguer vers le fragment de login
        // Navigation.findNavController(v).navigate(R.id.action_profile_to_login);
        Toast.makeText(requireContext(), "Déconnecté", Toast.LENGTH_SHORT).show();
    }

    // ──────────────────────────────────────────────
    //  Méthode utilitaire publique : sauvegarder le profil
    //  Appelle-la depuis un fragment d'édition
    // ──────────────────────────────────────────────
    public static void sauvegarderProfil(Context ctx,
                                         String nom, String email,
                                         String age, String groupeSanguin,
                                         String taille, String poids,
                                         String medecin, String allergies,
                                         String antecedents) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString("nom", nom)
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