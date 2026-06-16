package com.mobileproject.se77a.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class FragmentConfidentialite extends Fragment {

    private static final String PREFS_NAME        = "user_prefs";
    private static final String PREFS_PERM_ASKED  = "permissions_asked";
    private static final int    REQUEST_PERMISSIONS = 100;

    private ImageView iconCamera, iconLocalisation, iconNotifications, iconTelephone;
    private TextView  tvCamera,   tvLocalisation,   tvNotifications,   tvTelephone;
    private View btnSupprimerDonnees, btnPolitique;
    private View rowCamera, rowLocalisation, rowNotifications, rowTelephone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confidentialite, container, false);

        iconCamera        = view.findViewById(R.id.iconStatusCamera);
        iconLocalisation  = view.findViewById(R.id.iconStatusLocalisation);
        iconNotifications = view.findViewById(R.id.iconStatusNotifications);
        iconTelephone     = view.findViewById(R.id.iconStatusTelephone);

        tvCamera        = view.findViewById(R.id.tvStatusCamera);
        tvLocalisation  = view.findViewById(R.id.tvStatusLocalisation);
        tvNotifications = view.findViewById(R.id.tvStatusNotifications);
        tvTelephone     = view.findViewById(R.id.tvStatusTelephone);

        btnSupprimerDonnees = view.findViewById(R.id.btnSupprimerDonnees);
        btnPolitique        = view.findViewById(R.id.btnPolitique);

        rowCamera        = view.findViewById(R.id.rowCamera);
        rowLocalisation  = view.findViewById(R.id.rowLocalisation);
        rowNotifications = view.findViewById(R.id.rowNotifications);
        rowTelephone     = view.findViewById(R.id.rowTelephone);

        setupClickListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Rafraîchir l'UI uniquement
        setupPermissions();

        // Demander les permissions seulement si jamais demandées (stocké en SharedPreferences)
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean dejaDemandeees = prefs.getBoolean(PREFS_PERM_ASKED, false);

        if (!dejaDemandeees) {
            // Marquer comme demandées AVANT de lancer la popup
            prefs.edit().putBoolean(PREFS_PERM_ASKED, true).apply();
            demanderPermissionsManquantes();
        }
    }

    // ── Demander toutes les permissions non encore accordées ──
    private void demanderPermissionsManquantes() {
        List<String> aAccorder = new ArrayList<>();

        if (!isGranted(Manifest.permission.CAMERA))
            aAccorder.add(Manifest.permission.CAMERA);

        if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION))
            aAccorder.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !isGranted(Manifest.permission.POST_NOTIFICATIONS))
            aAccorder.add(Manifest.permission.POST_NOTIFICATIONS);

        if (!isGranted(Manifest.permission.CALL_PHONE))
            aAccorder.add(Manifest.permission.CALL_PHONE);

        if (!aAccorder.isEmpty()) {
            requestPermissions(aAccorder.toArray(new String[0]), REQUEST_PERMISSIONS);
        }
    }

    // ── Callback après réponse de l'utilisateur ──
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            setupPermissions(); // Rafraîchir l'UI seulement, sans aucune dialog
        }
    }

    private void setupClickListeners() {

        // Clic sur une row refusée → proposer d'ouvrir les paramètres
        rowCamera.setOnClickListener(v -> {
            if (!isGranted(Manifest.permission.CAMERA))
                afficherDialogParametres("La permission Caméra est requise pour photographier vos ordonnances.");
        });

        rowLocalisation.setOnClickListener(v -> {
            if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION))
                afficherDialogParametres("La permission Localisation est requise pour trouver les cabinets proches.");
        });

        rowNotifications.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && !isGranted(Manifest.permission.POST_NOTIFICATIONS))
                afficherDialogParametres("La permission Notifications est requise pour les rappels de médicaments.");
        });

        rowTelephone.setOnClickListener(v -> {
            if (!isGranted(Manifest.permission.CALL_PHONE))
                afficherDialogParametres("La permission Téléphone est requise pour appeler un cabinet directement.");
        });

        // Supprimer données
        btnSupprimerDonnees.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Supprimer mes données")
                        .setMessage("Cette action supprimera définitivement toutes vos données locales "
                                + "(rendez-vous, médicaments, ordonnances). Cette action est irréversible.")
                        .setPositiveButton("Supprimer", (dialog, which) -> supprimerToutesLesDonnees())
                        .setNegativeButton("Annuler", null)
                        .show()
        );

        // Politique
        btnPolitique.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Politique de confidentialité")
                        .setMessage(getPolitiqueTexte())
                        .setPositiveButton("Fermer", null)
                        .show()
        );
    }

    // ── Dialog simple pour ouvrir les paramètres Android ──
    private void afficherDialogParametres(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission requise")
                .setMessage(message + "\n\nActivez-la dans les paramètres de l'application.")
                .setPositiveButton("Ouvrir les paramètres", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package",
                            requireActivity().getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("Annuler", null) // fonctionne correctement car pas de logique après
                .show();
    }

    // ── Afficher l'état de chaque permission ──
    private void setupPermissions() {
        afficherEtatPermission(Manifest.permission.CAMERA, iconCamera, tvCamera);
        afficherEtatPermission(Manifest.permission.ACCESS_FINE_LOCATION, iconLocalisation, tvLocalisation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            afficherEtatPermission(Manifest.permission.POST_NOTIFICATIONS,
                    iconNotifications, tvNotifications);
        } else {
            setPermissionAccordee(iconNotifications, tvNotifications);
        }

        afficherEtatPermission(Manifest.permission.CALL_PHONE, iconTelephone, tvTelephone);
    }

    private boolean isGranted(String permission) {
        return ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void afficherEtatPermission(String permission, ImageView icon, TextView label) {
        if (isGranted(permission)) setPermissionAccordee(icon, label);
        else                       setPermissionRefusee(icon, label);
    }

    private void setPermissionAccordee(ImageView icon, TextView label) {
        icon.setImageResource(R.drawable.ic_check_circle);
        icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.permission_granted));
        label.setText("Accordée");
        label.setTextColor(ContextCompat.getColor(requireContext(), R.color.permission_granted));
    }

    private void setPermissionRefusee(ImageView icon, TextView label) {
        icon.setImageResource(R.drawable.ic_cancel);
        icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.permission_denied));
        label.setText("Refusée");
        label.setTextColor(ContextCompat.getColor(requireContext(), R.color.permission_denied));
    }

    private void supprimerToutesLesDonnees() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        AppDatabase db = AppDatabase.getInstance(requireContext());
        db.appointmentDao().deleteAll();
        db.medicationDao().deleteAll();

        Toast.makeText(getContext(),
                "Toutes vos données ont été supprimées.", Toast.LENGTH_LONG).show();
    }

    private String getPolitiqueTexte() {
        return "POLITIQUE DE CONFIDENTIALITÉ — HealthTracker\n\n" +
                "1. DONNÉES COLLECTÉES\n" +
                "HealthTracker collecte uniquement les données que vous saisissez : " +
                "informations de profil, rendez-vous médicaux, médicaments et ordonnances.\n\n" +
                "2. STOCKAGE LOCAL\n" +
                "Toutes vos données sont stockées localement sur votre appareil. " +
                "Aucune donnée n'est envoyée vers des serveurs externes sans votre consentement.\n\n" +
                "3. PERMISSIONS UTILISÉES\n" +
                "• Caméra : pour photographier vos ordonnances et résultats d'analyses.\n" +
                "• Localisation : pour géolocaliser les cabinets et pharmacies à proximité.\n" +
                "• Téléphone : pour appeler directement un cabinet médical depuis l'app.\n" +
                "• Notifications : pour vous rappeler la prise de médicaments.\n\n" +
                "4. PARTAGE DES DONNÉES\n" +
                "Vos données médicales ne sont jamais partagées avec des tiers.\n\n" +
                "5. SUPPRESSION\n" +
                "Vous pouvez supprimer toutes vos données à tout moment depuis cette page.";
    }
}