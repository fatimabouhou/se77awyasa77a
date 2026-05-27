package com.mobileproject.se77a.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.fragments.BottomNavFragment;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Récupérer le NavHostFragment et le NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // 2. Charger ton BottomNavFragment custom (si tu n'utilises pas une BottomNavigationView standard)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.bottom_nav_container, new BottomNavFragment())
                    .commit();
        }
    }

    // Exposer le NavController pour que le BottomNavFragment puisse changer d'écran
    public NavController getNavController() {
        return navController;
    }
}