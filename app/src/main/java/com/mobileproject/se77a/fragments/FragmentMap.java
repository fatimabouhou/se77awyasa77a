package com.mobileproject.se77a.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobileproject.se77a.R;

import java.util.Random;

public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                    enableMyLocation();
                    getDeviceLocation();
                } else {
                    Toast.makeText(requireContext(), "Permission refusée", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        view.findViewById(R.id.fab_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
            getDeviceLocation();
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void enableMyLocation() {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void getDeviceLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f));
                    generateMockPharmacies(userLatLng);
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void generateMockPharmacies(LatLng userPos) {
        googleMap.clear(); // Nettoyer les anciens marqueurs
        
        String[] prefixes = {"Pharmacie", "Grande Pharmacie", "Pharmacie de Garde", "Officine"};
        String[] suffixes = {"du Centre", "de la Paix", "des Oliviers", "Moderne", "Populaire", "Saint-Michel", "Al Baraka", "Atlas", "de l'Avenir", "du Soleil"};
        
        Random r = new Random();
        int count = 10; // On génère 10 pharmacies autour de vous

        for (int i = 0; i < count; i++) {
            String name = prefixes[r.nextInt(prefixes.length)] + " " + suffixes[r.nextInt(suffixes.length)];
            
            // Créer une position aléatoire dans un rayon de ~2-3km
            double lat = userPos.latitude + (r.nextDouble() - 0.5) / 40;
            double lng = userPos.longitude + (r.nextDouble() - 0.5) / 40;

            addPharmacyMarker(name, new LatLng(lat, lng));
        }
    }

    private void addPharmacyMarker(String name, LatLng position) {
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(name)
                .snippet("Ouverte actuellement")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleBottomNav(false);
    }

    private void toggleBottomNav(boolean visible) {
        if (getActivity() != null) {
            View nav = getActivity().findViewById(R.id.bottom_nav_container);
            if (nav != null) nav.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override public void onResume() { super.onResume(); mapView.onResume(); }
    @Override public void onPause() { super.onPause(); mapView.onPause(); }
    @Override public void onDestroy() { super.onDestroy(); toggleBottomNav(true); mapView.onDestroy(); }
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
