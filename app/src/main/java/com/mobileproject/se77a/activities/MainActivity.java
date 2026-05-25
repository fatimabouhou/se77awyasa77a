package com.mobileproject.se77a.activities; // À modifier selon ton projet

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.fragments.FragmentHome;
import com.mobileproject.se77a.fragments.FragmentTracking;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navRdv;
    private TextView tvNavHome, tvNavRdv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des boutons de navigation
        navHome = findViewById(R.id.nav_home);
        navRdv = findViewById(R.id.nav_rdv);
        tvNavHome = findViewById(R.id.tv_nav_home);
        tvNavRdv = findViewById(R.id.tv_nav_rdv);

        // Charger le premier fragment par défaut (Home)
        if (savedInstanceState == null) {
            loadFragment(new FragmentHome());
        }

        // Événement de clic sur Accueil
        navHome.setOnClickListener(v -> {
            loadFragment(new FragmentHome());
            updateBottomNavUI(true);
        });

        // Événement de clic sur Suivi (RDV)
        navRdv.setOnClickListener(v -> {
            loadFragment(new FragmentTracking());
            updateBottomNavUI(false);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Alterne les couleurs du texte pour simuler la sélection
    private void updateBottomNavUI(boolean isHomeSelected) {
        if (isHomeSelected) {
            tvNavHome.setTextColor(Color.parseColor("#6C5CE7")); // Violet sélectionné
            tvNavHome.setTypeface(null, android.graphics.Typeface.BOLD);
            tvNavRdv.setTextColor(Color.parseColor("#7B8FB0"));   // Gris neutre
            tvNavRdv.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            tvNavHome.setTextColor(Color.parseColor("#7B8FB0"));
            tvNavHome.setTypeface(null, android.graphics.Typeface.NORMAL);
            tvNavRdv.setTextColor(Color.parseColor("#6C5CE7"));
            tvNavRdv.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }
}