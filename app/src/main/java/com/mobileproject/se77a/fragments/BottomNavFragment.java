package com.mobileproject.se77a.fragments;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.activities.MainActivity;

public class BottomNavFragment extends Fragment {

    private ImageView icHome, icTracking, icMedication, icProfile;
    private TextView tvHome, tvTracking, tvMedication, tvProfile;

    private static final int COLOR_INACTIVE = 0xFF8E8E93;  // Gris iOS
    private static final int COLOR_ACTIVE = 0xFF007AFF;   // Bleu iOS

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_nav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation des layouts cliquables
        LinearLayout navHome = view.findViewById(R.id.nav_home);
        LinearLayout navRdv = view.findViewById(R.id.nav_rdv);
        LinearLayout navMedications = view.findViewById(R.id.nav_medications);
        LinearLayout navProfile = view.findViewById(R.id.nav_profile);

        // Initialisation des vues UI
        icHome = view.findViewById(R.id.ic_nav_home);
        icTracking = view.findViewById(R.id.ic_nav_rdv);
        icMedication = view.findViewById(R.id.ic_nav_medications);
        icProfile = view.findViewById(R.id.ic_nav_profile);

        tvHome = view.findViewById(R.id.tv_nav_home);
        tvTracking = view.findViewById(R.id.tv_nav_rdv);
        tvMedication = view.findViewById(R.id.tv_nav_medications);
        tvProfile = view.findViewById(R.id.tv_nav_profile);

        // Récupération du NavController depuis la MainActivity
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            NavController navController = mainActivity.getNavController();

            if (navController != null) {
                // 1. Écouter les changements de destination pour mettre à jour l'UI automatiquement
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    int id = destination.getId();
                    if (id == R.id.fragmentHome) {
                        updateUI("home");
                    } else if (id == R.id.fragmentTracking) {
                        updateUI("tracking");
                    } else if (id == R.id.fragmentMedications) {
                        updateUI("medications");
                    } else if (id == R.id.fragmentProfile) {
                        updateUI("profile");
                    }
                });

                // 2. Gérer les clics sur l'UI pour déclencher la navigation Jetpack
                navHome.setOnClickListener(v -> navController.navigate(R.id.fragmentHome));
                navRdv.setOnClickListener(v -> navController.navigate(R.id.fragmentTracking));
                navMedications.setOnClickListener(v -> navController.navigate(R.id.fragmentMedications));
                navProfile.setOnClickListener(v -> navController.navigate(R.id.fragmentProfile));
            }
        }
    }

    private void updateUI(String selected) {
        resetToInactive();

        switch (selected) {
            case "home":
                setActive(icHome, tvHome, R.drawable.ic_home_filled);
                break;
            case "tracking":
                setActive(icTracking, tvTracking, R.drawable.ic_tracking_filled);
                break;
            case "medications":
                setActive(icMedication, tvMedication, R.drawable.ic_medication_filled);
                break;
            case "profile":
                setActive(icProfile, tvProfile, R.drawable.ic_profile_filled);
                break;
        }
    }

    private void resetToInactive() {
        icHome.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_home_outline));
        icTracking.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_tracking_outline));
        icMedication.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_medication_outline));
        icProfile.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile_outline));

        icHome.setColorFilter(COLOR_INACTIVE, PorterDuff.Mode.SRC_IN);
        icTracking.setColorFilter(COLOR_INACTIVE, PorterDuff.Mode.SRC_IN);
        icMedication.setColorFilter(COLOR_INACTIVE, PorterDuff.Mode.SRC_IN);
        icProfile.setColorFilter(COLOR_INACTIVE, PorterDuff.Mode.SRC_IN);

        tvHome.setTextColor(COLOR_INACTIVE);
        tvTracking.setTextColor(COLOR_INACTIVE);
        tvMedication.setTextColor(COLOR_INACTIVE);
        tvProfile.setTextColor(COLOR_INACTIVE);

        tvHome.setTypeface(null, Typeface.NORMAL);
        tvTracking.setTypeface(null, Typeface.NORMAL);
        tvMedication.setTypeface(null, Typeface.NORMAL);
        tvProfile.setTypeface(null, Typeface.NORMAL);
    }

    private void setActive(ImageView icon, TextView text, int filledIconRes) {
        icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), filledIconRes));
        icon.setColorFilter(COLOR_ACTIVE, PorterDuff.Mode.SRC_IN);
        text.setTextColor(COLOR_ACTIVE);
        text.setTypeface(null, Typeface.BOLD);
    }
}