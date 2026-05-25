package com.mobileproject.se77a.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.mobileproject.se77a.R;

public class FragmentHome extends Fragment {

    private TextView tvUsername, tvHealthScore, tvRdvCount, tvMedCount, tvOrdoCount;
    private CardView btnNotification;
    private LinearLayout cardStatRdv, cardStatMed, cardStatOrdo;
    private LottieAnimationView lottieRunner;
    private View glowOuter, glowMid, glowInner;

    private ValueAnimator colorAnimator;
    private ValueAnimator glowAnimator;
    private int healthScore = 25;

    public FragmentHome() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvUsername      = view.findViewById(R.id.tv_username);
        tvHealthScore   = view.findViewById(R.id.tv_health_score);
        tvRdvCount      = view.findViewById(R.id.tv_rdv_count);
        tvMedCount      = view.findViewById(R.id.tv_med_count);
        tvOrdoCount     = view.findViewById(R.id.tv_ordo_count);
        btnNotification = view.findViewById(R.id.btn_notification);
        cardStatRdv     = view.findViewById(R.id.card_stat_rdv);
        cardStatMed     = view.findViewById(R.id.card_stat_med);
        cardStatOrdo    = view.findViewById(R.id.card_stat_ordo);
        lottieRunner    = view.findViewById(R.id.lottie_runner);
        glowOuter       = view.findViewById(R.id.glow_outer);
        glowMid         = view.findViewById(R.id.glow_mid);
        glowInner       = view.findViewById(R.id.glow_inner);

        tvHealthScore.setText(String.valueOf(healthScore));

        lancerAnimationRunner(healthScore);
        lancerAnimationGlow();

        btnNotification.setOnClickListener(v ->
                Toast.makeText(getContext(), "Notifications", Toast.LENGTH_SHORT).show());
        cardStatRdv.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détails RDV", Toast.LENGTH_SHORT).show());
        cardStatMed.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détails Médicaments", Toast.LENGTH_SHORT).show());
        cardStatOrdo.setOnClickListener(v ->
                Toast.makeText(getContext(), "Détails Ordonnances", Toast.LENGTH_SHORT).show());

        return view;
    }

    /**
     * Coloration néon animée du runner : cyan → bleu → violet → cyan
     */
    private void lancerAnimationRunner(int score) {
        Integer[] couleurs;

        if (score >= 80) {
            couleurs = new Integer[]{
                    Color.parseColor("#00D4FF"), // cyan néon
                    Color.parseColor("#0099FF"), // bleu électrique
                    Color.parseColor("#7C3AED"), // violet
                    Color.parseColor("#00D4FF")
            };
        } else if (score >= 60) {
            couleurs = new Integer[]{
                    Color.parseColor("#3B82F6"),
                    Color.parseColor("#8B5CF6"),
                    Color.parseColor("#06B6D4"),
                    Color.parseColor("#3B82F6")
            };
        } else if (score >= 40) {
            couleurs = new Integer[]{
                    Color.parseColor("#F59E0B"),
                    Color.parseColor("#EF4444"),
                    Color.parseColor("#F97316"),
                    Color.parseColor("#F59E0B")
            };
        } else {
            couleurs = new Integer[]{
                    Color.parseColor("#EF4444"),
                    Color.parseColor("#F43F5E"),
                    Color.parseColor("#DC2626"),
                    Color.parseColor("#EF4444")
            };
        }

        if (colorAnimator != null) colorAnimator.cancel();

        colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), (Object[]) couleurs);
        colorAnimator.setDuration(3000);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);

        colorAnimator.addUpdateListener(animator -> {
            int c = (int) animator.getAnimatedValue();
            lottieRunner.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR,
                    frameInfo -> c
            );
        });

        colorAnimator.start();
    }

    /**
     * Animation du glow : pulse d'alpha — effet respiration lumineuse
     */
    private void lancerAnimationGlow() {
        if (glowAnimator != null) glowAnimator.cancel();

        glowAnimator = ValueAnimator.ofFloat(0.2f, 1.0f);
        glowAnimator.setDuration(1500);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);

        glowAnimator.addUpdateListener(animator -> {
            float alpha = (float) animator.getAnimatedValue();
            // Chaque cercle pulse à une intensité différente — effet de profondeur
            if (glowOuter != null) glowOuter.setAlpha(alpha * 0.4f);
            if (glowMid   != null) glowMid.setAlpha(alpha * 0.65f);
            if (glowInner != null) glowInner.setAlpha(alpha);
        });

        glowAnimator.start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.chart_container, new FragmentVisitsChart())
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (colorAnimator != null) { colorAnimator.cancel(); colorAnimator = null; }
        if (glowAnimator  != null) { glowAnimator.cancel();  glowAnimator  = null; }
    }
}