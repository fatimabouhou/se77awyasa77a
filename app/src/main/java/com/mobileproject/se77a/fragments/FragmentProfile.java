package com.mobileproject.se77a.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.activities.LoginActivity;
import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.dao.AppointmentDao.MedicalPair;

import java.util.List;

public class FragmentProfile extends Fragment {

    private static final String PREFS_NAME = "user_prefs";

    private TextView tvAvatar, tvNom, tvEmail, tvAge, tvGroupeSanguin, tvMorphologie;
    private TextView tvStatConsultations, tvStatMedicaments, tvStatOrdonnances;
    private ImageButton btnSettings;
    private LinearLayout containerMaladiesMedecins;
    private RelativeLayout itemOrdonnances, itemConfidentialite, itemDeconnexion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvAvatar        = view.findViewById(R.id.tvAvatar);
        tvNom           = view.findViewById(R.id.tvNom);
        tvEmail         = view.findViewById(R.id.tvEmail);
        tvAge           = view.findViewById(R.id.tvAge);
        tvGroupeSanguin = view.findViewById(R.id.tvGroupeSanguin);
        tvMorphologie   = view.findViewById(R.id.tvMorphologie);

        tvStatConsultations = view.findViewById(R.id.tvStatConsultations);
        tvStatMedicaments   = view.findViewById(R.id.tvStatMedicaments);
        tvStatOrdonnances   = view.findViewById(R.id.tvStatOrdonnances);

        containerMaladiesMedecins = view.findViewById(R.id.containerMaladiesMedecins);
        btnSettings               = view.findViewById(R.id.btnSettings);

        itemOrdonnances     = view.findViewById(R.id.itemOrdonnances);
        itemConfidentialite = view.findViewById(R.id.itemConfidentialite);
        itemDeconnexion     = view.findViewById(R.id.itemDeconnexion);

        setupClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        chargerProfil();
        chargerStatsEtSuiviMedical();
    }

    private void setupClickListeners() {
        btnSettings.setOnClickListener(v ->
                Toast.makeText(getContext(), "Paramètres", Toast.LENGTH_SHORT).show());

        itemOrdonnances.setOnClickListener(v ->
                Toast.makeText(getContext(), "Mes Ordonnances", Toast.LENGTH_SHORT).show());

        itemConfidentialite.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.fragmentConfidentialite));

        itemDeconnexion.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Toast.makeText(getContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void chargerProfil() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String nom           = prefs.getString("current_user_name", "Utilisateur");
        String email         = prefs.getString("email", "—");
        String age           = prefs.getString("age", "");
        String groupeSanguin = prefs.getString("groupe_sanguin", "");
        String taille        = prefs.getString("taille", "");
        String poids         = prefs.getString("poids", "");

        tvNom.setText(nom);
        tvEmail.setText(email);
        tvAvatar.setText(extraireInitiales(nom));

        tvAge.setText((age.isEmpty() || age.equals("—") || age.equals("0"))
                ? "— ans" : age + " ans");
        tvGroupeSanguin.setText((groupeSanguin.isEmpty() || groupeSanguin.equals("—"))
                ? "Groupe —" : "Groupe " + groupeSanguin);

        if (taille.isEmpty() || poids.isEmpty() || taille.equals("—")
                || poids.equals("—") || taille.equals("0")) {
            tvMorphologie.setText("— cm · — kg");
        } else {
            tvMorphologie.setText(taille + " cm · " + poids + " kg");
        }
    }

    private void chargerStatsEtSuiviMedical() {
        AppDatabase db = AppDatabase.getInstance(requireContext());

        tvStatConsultations.setText(String.valueOf(db.appointmentDao().countAppointments()));
        tvStatMedicaments.setText(String.valueOf(db.medicationDao().countMedications()));
        tvStatOrdonnances.setText(String.valueOf(db.appointmentDao().countOrdonnances()));

        containerMaladiesMedecins.removeAllViews();

        List<MedicalPair> listSuivi = db.appointmentDao().getAllDiseasesWithDoctors();
        if (listSuivi.isEmpty()) {
            ajouterLigneMedicaleDynamique("Suivi médical", "Aucun médecin enregistré");
        } else {
            for (MedicalPair item : listSuivi) {
                String specialite = (item.specialty == null || item.specialty.isEmpty())
                        ? "Consultation générale" : item.specialty;
                ajouterLigneMedicaleDynamique(specialite, "Dr. " + item.doctorName);
            }
        }
    }

    private void ajouterLigneMedicaleDynamique(String titreSpecialite, String nomDocteur) {
        float dp = getResources().getDisplayMetrics().density;
        int padding = (int) (14 * dp);

        RelativeLayout row = new RelativeLayout(requireContext());
        row.setPadding(padding, padding, padding, padding);
        row.setBackgroundResource(R.drawable.bg_row_divider);

        ImageView icon = new ImageView(requireContext());
        icon.setId(View.generateViewId());
        icon.setImageResource(R.drawable.ic_health);
        icon.setColorFilter(Color.parseColor("#018ABE"));
        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(
                (int) (20 * dp), (int) (20 * dp));
        iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
        iconParams.setMarginEnd((int) (10 * dp));
        row.addView(icon, iconParams);

        TextView tvTitre = new TextView(requireContext());
        tvTitre.setText(titreSpecialite);
        tvTitre.setTextColor(Color.parseColor("#02457A"));
        tvTitre.setTextSize(13);
        RelativeLayout.LayoutParams titreParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        titreParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titreParams.addRule(RelativeLayout.END_OF, icon.getId());
        row.addView(tvTitre, titreParams);

        TextView tvDoc = new TextView(requireContext());
        tvDoc.setText(nomDocteur);
        tvDoc.setTextColor(Color.parseColor("#001B48"));
        tvDoc.setTextSize(13);
        tvDoc.setTypeface(null, Typeface.BOLD);
        RelativeLayout.LayoutParams docParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        docParams.addRule(RelativeLayout.CENTER_VERTICAL);
        docParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        row.addView(tvDoc, docParams);

        containerMaladiesMedecins.addView(row);
    }

    private String extraireInitiales(String nomComplet) {
        if (nomComplet == null || nomComplet.trim().isEmpty()) return "U";
        String[] mots = nomComplet.trim().split("\\s+");
        if (mots.length >= 2)
            return (mots[0].substring(0, 1) + mots[1].substring(0, 1)).toUpperCase();
        return mots[0].substring(0, 1).toUpperCase();
    }
}