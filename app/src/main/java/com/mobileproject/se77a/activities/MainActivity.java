package com.mobileproject.se77a.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.fragments.BottomNavFragment;
import com.mobileproject.se77a.fragments.FragmentHome;
import com.mobileproject.se77a.fragments.FragmentTracking;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome, navRdv;
    private TextView tvNavHome, tvNavRdv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Fragment de contenu par défaut
            loadFragment(new FragmentHome());

            // ✅ Charger le BottomNavFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.bottom_nav_container, new BottomNavFragment())
                    .commit();
        }
    }

    // ✅ public pour que BottomNavFragment puisse l'appeler
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Gardée si utilisée ailleurs dans ton projet
    private void updateBottomNavUI(boolean isHomeSelected) {
        if (isHomeSelected) {
            tvNavHome.setTextColor(Color.parseColor("#6C5CE7"));
            tvNavHome.setTypeface(null, android.graphics.Typeface.BOLD);
            tvNavRdv.setTextColor(Color.parseColor("#7B8FB0"));
            tvNavRdv.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            tvNavHome.setTextColor(Color.parseColor("#7B8FB0"));
            tvNavHome.setTypeface(null, android.graphics.Typeface.NORMAL);
            tvNavRdv.setTextColor(Color.parseColor("#6C5CE7"));
            tvNavRdv.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }
}