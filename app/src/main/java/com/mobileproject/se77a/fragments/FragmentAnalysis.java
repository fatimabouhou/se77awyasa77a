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
import com.mobileproject.se77a.adapters.AnalysisAdapter;
import com.mobileproject.se77a.database.entities.Analysis;
import com.mobileproject.se77a.repository.AnalysisRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FragmentAnalysis extends Fragment {

    private static final String KEY_PHOTO_PATH = "current_photo_path";

    private AnalysisRepository             analysisRepository;
    private AnalysisAdapter                adapter;
    private String                         currentPhotoPath;
    private TextView                       tvEmpty;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restaurer le chemin si le fragment a été recréé (rotation écran, etc.)
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(KEY_PHOTO_PATH);
        }

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        promptForAnalysisTitle();
                    } else {
                        if (currentPhotoPath != null) {
                            new File(currentPhotoPath).delete();
                            currentPhotoPath = null;
                        }
                        Toast.makeText(getContext(), "Capture annulée", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

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

        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        analysisRepository = new AnalysisRepository(requireActivity().getApplication());

        RecyclerView rv = view.findViewById(R.id.rv_analyses);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        tvEmpty = view.findViewById(R.id.tv_empty_analyses);

        adapter = new AnalysisAdapter(new AnalysisAdapter.OnAnalysisClickListener() {
            @Override
            public void onClick(Analysis analysis) {
                showFullScreenImage(analysis);
            }

            @Override
            public void onLongClick(Analysis analysis) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Supprimer")
                        .setMessage("Voulez-vous supprimer cette analyse ?")
                        .setPositiveButton("Supprimer", (d, w) -> {
                            if (analysis.imagePath != null) {
                                new File(analysis.imagePath).delete();
                            }
                            analysisRepository.delete(analysis);
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        });

        rv.setAdapter(adapter);

        analysisRepository.getAllAnalyses().observe(getViewLifecycleOwner(), list -> {
            adapter.setAnalyses(list);
            tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });

        CardView btnCamera = view.findViewById(R.id.btn_take_analysis_photo);
        if (btnCamera != null) {
            btnCamera.setOnClickListener(v -> launchCamera());
        }

        return view;
    }

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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Impossible de lancer la caméra", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName  = "ANALYSIS_" + timeStamp;
        File   storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File   image      = File.createTempFile(fileName, ".jpg", storageDir);
        currentPhotoPath  = image.getAbsolutePath();
        return image;
    }

    private void promptForAnalysisTitle() {
        if (currentPhotoPath == null) {
            Toast.makeText(getContext(), "Erreur : chemin de la photo introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        File photoFile = new File(currentPhotoPath);
        if (!photoFile.exists()) {
            Toast.makeText(getContext(), "Erreur : la photo n'a pas été sauvegardée correctement", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nouvelle Analyse");
        builder.setMessage("Entrez un libellé (ex: Bilan sanguin Dr. Martin) :");

        final EditText input = new EditText(requireContext());
        input.setHint("Nom de l'analyse");
        builder.setView(input);

        final String photoPath = currentPhotoPath;

        builder.setPositiveButton("Sauvegarder", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (title.isEmpty()) {
                title = "Analyse sans nom";
            }

            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            Analysis a = new Analysis(0, photoPath, date, title);

            analysisRepository.insert(a);
            currentPhotoPath = null;
            Toast.makeText(getContext(), "✅ Analyse sauvegardée !", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> {
            new File(photoPath).delete();
            currentPhotoPath = null;
            dialog.cancel();
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void showFullScreenImage(Analysis analysis) {
        if (analysis.imagePath == null) return;

        File f = new File(analysis.imagePath);
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