package com.mobileproject.se77a.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.api.ApiService;
import com.mobileproject.se77a.api.HealthTip;
import com.mobileproject.se77a.api.RetrofitClient;
import com.mobileproject.se77a.api.TranslationResponse;

import java.util.List;
import java.util.Random;

import retrofit2.Response;

public class FragmentHealthTip extends Fragment {

    private TextView tvTipTitle, tvTipContent, tvDifficulty, tvMuscle, tvFullInstructions, tvEquipment, tvLearnMoreLabel, tvSafetyInfo, tvSafetyLabel;
    private View layoutExpanded;
    private View btnLearnMore;
    private ImageView ivArrowToggle;
    private boolean isExpanded = false;

    private static final String TAG = "FragmentHealthTip";
    private final String API_KEY = "yfBSLnxmwWH5G8HY2i206SG4h0xWWJu6DpDw8EQ0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_health_tip_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTipTitle = view.findViewById(R.id.tv_tip_title);
        tvTipContent = view.findViewById(R.id.tv_tip_content);
        tvDifficulty = view.findViewById(R.id.tv_difficulty_badge);
        tvMuscle = view.findViewById(R.id.tv_muscle_targeted);
        tvFullInstructions = view.findViewById(R.id.tv_full_instructions);
        tvEquipment = view.findViewById(R.id.tv_equipment);
        tvLearnMoreLabel = view.findViewById(R.id.tv_learn_more_label);
        tvSafetyInfo = view.findViewById(R.id.tv_safety_info);
        tvSafetyLabel = view.findViewById(R.id.tv_safety_label);
        layoutExpanded = view.findViewById(R.id.layout_expanded_content);
        btnLearnMore = view.findViewById(R.id.btn_learn_more);
        ivArrowToggle = view.findViewById(R.id.iv_arrow_toggle);

        btnLearnMore.setOnClickListener(v -> toggleExpand());

        new FetchAndTranslateTask().execute();
    }

    private void toggleExpand() {
        isExpanded = !isExpanded;
        layoutExpanded.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        tvLearnMoreLabel.setText(isExpanded ? "Voir moins" : "En savoir plus");
        ivArrowToggle.setRotation(isExpanded ? 270 : 90); // Rotation de la flèche
    }

    private class FetchAndTranslateTask extends AsyncTask<Void, Void, HealthTip> {
        @Override
        protected HealthTip doInBackground(Void... voids) {
            try {
                ApiService service = RetrofitClient.getApiService();
                
                // 1. Récupérer l'exercice (Anglais)
                String[] categories = {"cardio", "stretching", "strength", "plyometrics"};
                String randomCat = categories[new Random().nextInt(categories.length)];
                Log.d(TAG, "Fetching exercise for type: " + randomCat);
                Response<List<HealthTip>> response = service.getExercises(API_KEY, randomCat).execute();
                
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d(TAG, "Successfully fetched " + response.body().size() + " exercises");
                    HealthTip exercise = response.body().get(new Random().nextInt(response.body().size()));
                    
                    // 2. Traduire le nom
                    Log.d(TAG, "Translating title: " + exercise.getName());
                    Response<TranslationResponse> trTitle = service.translate(exercise.getName(), "en|fr").execute();
                    if (trTitle.isSuccessful() && trTitle.body() != null) {
                        exercise.setName(trTitle.body().getTranslatedText());
                    }

                    // 3. Traduire les instructions
                    String instr = exercise.getInstructions();
                    if (instr != null) {
                        Log.d(TAG, "Translating instructions (length: " + instr.length() + ")");
                        if (instr.length() > 300) instr = instr.substring(0, 300); // MyMemory limit
                        Response<TranslationResponse> trContent = service.translate(instr, "en|fr").execute();
                        if (trContent.isSuccessful() && trContent.body() != null) {
                            exercise.setInstructions(trContent.body().getTranslatedText());
                        }
                    }

                    // 4. Traduire les infos de sécurité
                    String safety = exercise.getSafetyInfo();
                    if (safety != null && !safety.isEmpty()) {
                        Log.d(TAG, "Translating safety info...");
                        if (safety.length() > 300) safety = safety.substring(0, 300);
                        Response<TranslationResponse> trSafety = service.translate(safety, "en|fr").execute();
                        if (trSafety.isSuccessful() && trSafety.body() != null) {
                            exercise.setSafetyInfo(trSafety.body().getTranslatedText());
                        }
                    }

                    return exercise;
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " " + response.message());
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in FetchAndTranslateTask", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(HealthTip exercise) {
            if (!isAdded()) return;

            if (exercise != null) {
                // Titre et badges
                tvTipTitle.setText(exercise.getName());
                tvDifficulty.setText(capitalize(exercise.getDifficulty()));
                tvMuscle.setText(capitalize(exercise.getMuscle()));
                
                // Instructions
                String instr = exercise.getInstructions();
                tvFullInstructions.setText(instr);
                
                // Équipement
                if (exercise.getEquipment() != null && !exercise.getEquipment().isEmpty()) {
                    tvEquipment.setText("Équipement : " + exercise.getEquipment());
                    tvEquipment.setVisibility(View.VISIBLE);
                } else {
                    tvEquipment.setVisibility(View.GONE);
                }

                // Infos sécurité (optionnel)
                if (exercise.getSafetyInfo() != null && !exercise.getSafetyInfo().isEmpty()) {
                    tvSafetyInfo.setText(exercise.getSafetyInfo());
                    tvSafetyInfo.setVisibility(View.VISIBLE);
                    tvSafetyLabel.setVisibility(View.VISIBLE);
                } else {
                    tvSafetyInfo.setVisibility(View.GONE);
                    tvSafetyLabel.setVisibility(View.GONE);
                }

                // Aperçu court (max 2 lignes dans l'XML)
                if (instr != null && instr.length() > 100) {
                    tvTipContent.setText(instr.substring(0, 97) + "...");
                } else {
                    tvTipContent.setText(instr);
                }
            } else {
                tvTipTitle.setText("Conseil Santé");
                tvTipContent.setText("Une activité physique régulière améliore votre forme !");
                tvDifficulty.setVisibility(View.GONE);
                tvMuscle.setText("Général");
                btnLearnMore.setVisibility(View.GONE);
            }
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) return "";
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }
}
