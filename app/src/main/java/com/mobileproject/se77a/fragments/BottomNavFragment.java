package com.mobileproject.se77a.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobileproject.se77a.R;
import com.mobileproject.se77a.activities.MainActivity;

public class BottomNavFragment extends Fragment {

    private TextView tvNavHome, tvNavRdv, tvNavMedications, tvNavProfile;

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

        LinearLayout navHome        = view.findViewById(R.id.nav_home);
        LinearLayout navRdv         = view.findViewById(R.id.nav_rdv);
        LinearLayout navMedications = view.findViewById(R.id.nav_medications);
        LinearLayout navProfile     = view.findViewById(R.id.nav_profile);

        tvNavHome        = view.findViewById(R.id.tv_nav_home);
        tvNavRdv         = view.findViewById(R.id.tv_nav_rdv);
        tvNavMedications = view.findViewById(R.id.tv_nav_medications);
        tvNavProfile     = view.findViewById(R.id.tv_nav_profile);

        navHome.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new FragmentHome());
            updateUI("home");
        });

        navRdv.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new FragmentTracking());
            updateUI("rdv");
        });

        navMedications.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new FragmentMedications());
            updateUI("medications");
        });

        navProfile.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new FragmentProfile());
            updateUI("profile");
        });
    }

    private void updateUI(String selected) {
        // Reset tout
        tvNavHome.setTextColor(Color.parseColor("#7B8FB0"));
        tvNavHome.setTypeface(null, Typeface.NORMAL);
        tvNavRdv.setTextColor(Color.parseColor("#7B8FB0"));
        tvNavRdv.setTypeface(null, Typeface.NORMAL);
        tvNavMedications.setTextColor(Color.parseColor("#7B8FB0"));
        tvNavMedications.setTypeface(null, Typeface.NORMAL);
        tvNavProfile.setTextColor(Color.parseColor("#7B8FB0"));
        tvNavProfile.setTypeface(null, Typeface.NORMAL);

        // Activer le tab sélectionné
        switch (selected) {
            case "home":
                tvNavHome.setTextColor(Color.parseColor("#6C5CE7"));
                tvNavHome.setTypeface(null, Typeface.BOLD);
                break;
            case "rdv":
                tvNavRdv.setTextColor(Color.parseColor("#6C5CE7"));
                tvNavRdv.setTypeface(null, Typeface.BOLD);
                break;
            case "medications":
                tvNavMedications.setTextColor(Color.parseColor("#6C5CE7"));
                tvNavMedications.setTypeface(null, Typeface.BOLD);
                break;
            case "profile":
                tvNavProfile.setTextColor(Color.parseColor("#6C5CE7"));
                tvNavProfile.setTypeface(null, Typeface.BOLD);
                break;
        }
    }
}