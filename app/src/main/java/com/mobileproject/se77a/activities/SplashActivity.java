package com.mobileproject.se77a.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileproject.se77a.R;

public class SplashActivity extends AppCompatActivity {

    // Durée d'affichage de l'écran avant la redirection automatique (en millisecondes)
    private static final int AUTO_NAVIGATE_DELAY = 3500;

    private Handler handler;
    private Runnable autoNavigateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mode plein écran : cache la barre de statut
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        // Références aux vues XML
        TextView tvTitle    = findViewById(R.id.txtTitle);
        ImageView ivDoctor  = findViewById(R.id.imgDoctor);
        ImageView ivPills   = findViewById(R.id.imgPills);
        ImageView ivSyringe = findViewById(R.id.imgSyringe);
        View     bgCircle   = findViewById(R.id.bg_circle);

        // ----- Initialisation des états pour l'animation -----

        // Le titre démarre un peu plus haut et invisible
        tvTitle.setAlpha(0f);
        tvTitle.setTranslationY(-60f);

        // Le médecin démarre un peu plus bas et invisible
        ivDoctor.setAlpha(0f);
        ivDoctor.setTranslationY(80f);

        // Les éléments décoratifs démarrent petits et invisibles
        ivPills.setAlpha(0f);
        ivPills.setScaleX(0.5f);
        ivPills.setScaleY(0.5f);

        ivSyringe.setAlpha(0f);
        ivSyringe.setScaleX(0.5f);
        ivSyringe.setScaleY(0.5f);

        // Le cercle de fond démarre discret
        bgCircle.setAlpha(0f);
        bgCircle.setScaleX(0.8f);
        bgCircle.setScaleY(0.8f);

        // ----- Création des ObjectAnimators -----

        // Animation du cercle de fond
        ObjectAnimator bgAlpha   = ObjectAnimator.ofFloat(bgCircle, "alpha", 0f, 0.25f).setDuration(600);
        ObjectAnimator bgScaleX  = ObjectAnimator.ofFloat(bgCircle, "scaleX", 0.8f, 1f).setDuration(600);
        ObjectAnimator bgScaleY  = ObjectAnimator.ofFloat(bgCircle, "scaleY", 0.8f, 1f).setDuration(600);

        // Animation du titre
        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f).setDuration(500);
        ObjectAnimator titleY     = ObjectAnimator.ofFloat(tvTitle, "translationY", -60f, 0f).setDuration(500);

        // Animation du médecin
        ObjectAnimator doctorAlpha = ObjectAnimator.ofFloat(ivDoctor, "alpha", 0f, 1f).setDuration(700);
        ObjectAnimator doctorY     = ObjectAnimator.ofFloat(ivDoctor, "translationY", 80f, 0f).setDuration(700);

        // Animation des pilules
        ObjectAnimator pillsAlpha  = ObjectAnimator.ofFloat(ivPills, "alpha", 0f, 1f).setDuration(500);
        ObjectAnimator pillsScaleX = ObjectAnimator.ofFloat(ivPills, "scaleX", 0.5f, 1f).setDuration(500);
        ObjectAnimator pillsScaleY = ObjectAnimator.ofFloat(ivPills, "scaleY", 0.5f, 1f).setDuration(500);

        // Animation de la seringue
        ObjectAnimator syringeAlpha  = ObjectAnimator.ofFloat(ivSyringe, "alpha", 0f, 1f).setDuration(500);
        ObjectAnimator syringeScaleX = ObjectAnimator.ofFloat(ivSyringe, "scaleX", 0.5f, 1f).setDuration(500);
        ObjectAnimator syringeScaleY = ObjectAnimator.ofFloat(ivSyringe, "scaleY", 0.5f, 1f).setDuration(500);

        // Configuration des interpolateurs (effets de rebond et fluidité)
        AccelerateDecelerateInterpolator smooth = new AccelerateDecelerateInterpolator();
        OvershootInterpolator overshoot = new OvershootInterpolator(1.2f);

        doctorY.setInterpolator(overshoot);
        pillsScaleX.setInterpolator(overshoot);
        pillsScaleY.setInterpolator(overshoot);
        syringeScaleX.setInterpolator(overshoot);
        syringeScaleY.setInterpolator(overshoot);
        titleY.setInterpolator(smooth);

        // Séquencement temporel (Delays)
        bgAlpha.setStartDelay(0);
        bgScaleX.setStartDelay(0);
        bgScaleY.setStartDelay(0);
        titleAlpha.setStartDelay(200);
        titleY.setStartDelay(200);
        doctorAlpha.setStartDelay(400);
        doctorY.setStartDelay(400);
        pillsAlpha.setStartDelay(700);
        pillsScaleX.setStartDelay(700);
        pillsScaleY.setStartDelay(700);
        syringeAlpha.setStartDelay(850);
        syringeScaleX.setStartDelay(850);
        syringeScaleY.setStartDelay(850);

        // Lancement global des animations d'entrée
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(
                bgAlpha, bgScaleX, bgScaleY,
                titleAlpha, titleY,
                doctorAlpha, doctorY,
                pillsAlpha, pillsScaleX, pillsScaleY,
                syringeAlpha, syringeScaleX, syringeScaleY
        );
        animSet.start();

        // Animation en boucle : lévitation du médecin
        startFloatingAnimation(ivDoctor);

        // Configuration du minuteur de redirection automatique
        handler = new Handler(Looper.getMainLooper());
        autoNavigateRunnable = this::navigateToMain;
        handler.postDelayed(autoNavigateRunnable, AUTO_NAVIGATE_DELAY);
    }

    /** Animation de lévitation infinie */
    private void startFloatingAnimation(View view) {
        ObjectAnimator floatUp = ObjectAnimator.ofFloat(view, "translationY", 0f, -18f);
        floatUp.setDuration(1800);
        floatUp.setRepeatCount(ObjectAnimator.INFINITE);
        floatUp.setRepeatMode(ObjectAnimator.REVERSE);
        floatUp.setInterpolator(new AccelerateDecelerateInterpolator());
        floatUp.setStartDelay(1200);
        floatUp.start();
    }

    /** Changement d'écran vers la MainActivity avec transition propre */
    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sécurité pour éviter les fuites de mémoire si l'activité s'arrête avant la fin du timer
        if (handler != null && autoNavigateRunnable != null) {
            handler.removeCallbacks(autoNavigateRunnable);
        }
    }
}