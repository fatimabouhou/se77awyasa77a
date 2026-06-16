package com.mobileproject.se77a.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.adapters.PrescriptionAdapter;
import com.mobileproject.se77a.database.entities.Prescription;
import com.mobileproject.se77a.repository.PrescriptionRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentPrescription extends Fragment {

    // ── Clé pour sauvegarder le chemin de la photo en cas de recréation du fragment ──
    private static final String KEY_PHOTO_PATH = "current_photo_path";

    private PrescriptionRepository prescriptionRepository;
    private PrescriptionAdapter    adapter;
    private String                 currentPhotoPath;
    private TextView               tvEmpty;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── CORRECTION : Restaurer currentPhotoPath si le fragment a été recréé ──
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(KEY_PHOTO_PATH);
        }

        // Initialisation du launcher dans onCreate (obligatoire avant le démarrage du cycle de vie)
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Photo prise avec succès → demander le libellé
                        promptForPrescriptionTitle();
                    } else {
                        // Annulation → supprimer le fichier temporaire créé
                        if (currentPhotoPath != null) {
                            new File(currentPhotoPath).delete();
                            currentPhotoPath = null;
                        }
                        Toast.makeText(getContext(), "Capture annulée", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // ── CORRECTION : Sauvegarder currentPhotoPath pour survivre aux rotations d'écran ──
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentPhotoPath != null) {
            outState.putString(KEY_PHOTO_PATH, currentPhotoPath);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prescription, container, false);

        prescriptionRepository = new PrescriptionRepository(requireActivity().getApplication());

        RecyclerView rv = view.findViewById(R.id.rv_prescriptions);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        tvEmpty = view.findViewById(R.id.tv_empty_prescriptions);

        adapter = new PrescriptionAdapter(new PrescriptionAdapter.OnPrescriptionClickListener() {
            @Override
            public void onClick(Prescription prescription) {
                showFullScreenImage(prescription);
            }

            @Override
            public void onLongClick(Prescription prescription) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Supprimer")
                        .setMessage("Voulez-vous supprimer cette ordonnance ?")
                        .setPositiveButton("Supprimer", (d, w) -> {
                            if (prescription.imagePath != null) {
                                new File(prescription.imagePath).delete();
                            }
                            prescriptionRepository.delete(prescription);
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        });

        rv.setAdapter(adapter);

        // Observer la base de données : la liste se met à jour automatiquement
        prescriptionRepository.getAllPrescriptions().observe(getViewLifecycleOwner(), list -> {
            adapter.setPrescriptions(list);
            tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // Bouton pour photographier une ordonnance
        CardView btnCamera = view.findViewById(R.id.btn_take_prescription_photo);
        if (btnCamera != null) {
            btnCamera.setOnClickListener(v -> launchCamera());
        }

        return view;
    }

    // ── Lancement de la caméra avec FileProvider ───────────────────────────
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            // Accorder la permission de lecture/écriture à l'application caméra
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            cameraLauncher.launch(intent);

        } catch (IOException e) {
            Toast.makeText(getContext(), "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Impossible de lancer la caméra", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Création du fichier image temporaire ──────────────────────────────
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName  = "PRESCRIPTION_" + timeStamp;
        File   storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File   image      = File.createTempFile(fileName, ".jpg", storageDir);

        // ── IMPORTANT : Stocker le chemin absolu avant de lancer la caméra ──
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // ── Boîte de dialogue pour saisir le libellé de l'ordonnance ──────────
    private void promptForPrescriptionTitle() {

        // Vérification de sécurité : si le chemin est null, on ne peut pas sauvegarder
        if (currentPhotoPath == null) {
            Toast.makeText(getContext(), "Erreur : chemin de la photo introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier que le fichier photo existe réellement
        File photoFile = new File(currentPhotoPath);
        if (!photoFile.exists()) {
            Toast.makeText(getContext(), "Erreur : la photo n'a pas été sauvegardée correctement", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nouvelle Ordonnance");
        builder.setMessage("Entrez un libellé (ex: Consultation Dr. Martin) :");

        final EditText input = new EditText(requireContext());
        input.setHint("Nom de l'ordonnance");
        builder.setView(input);

        // Copie locale pour éviter les problèmes de capture dans le lambda
        final String photoPath = currentPhotoPath;

        builder.setPositiveButton("Sauvegarder", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (title.isEmpty()) {
                title = "Ordonnance sans nom";
            }

            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            Prescription p = new Prescription(0, photoPath, date, title);

            prescriptionRepository.insert(p);
            currentPhotoPath = null; // Réinitialiser après sauvegarde
            Toast.makeText(getContext(), "✅ Ordonnance sauvegardée !", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> {
            // Supprimer le fichier photo si l'utilisateur annule
            new File(photoPath).delete();
            currentPhotoPath = null;
            dialog.cancel();
        });

        builder.setCancelable(false);
        builder.show();
    }

    // ── Affichage plein écran de l'ordonnance ─────────────────────────────
    private void showFullScreenImage(Prescription prescription) {
        if (prescription.imagePath == null) return;

        File f = new File(prescription.imagePath);
        if (!f.exists()) {
            Toast.makeText(getContext(), "Image introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_prescription_fullscreen, null);

        android.widget.ImageView iv = dialogView.findViewById(R.id.iv_fullscreen);
        iv.setImageURI(Uri.fromFile(f));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}