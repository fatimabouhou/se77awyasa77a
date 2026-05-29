package com.mobileproject.se77a.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mobileproject.se77a.R;

import java.util.ArrayList;

public class FragmentVisitsChart extends Fragment {

    public FragmentVisitsChart() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_visits_chart, container, false);

        LineChart lineChart = view.findViewById(R.id.lineChart);
        setupChart(lineChart);

        return view;
    }

    private void setupChart(LineChart lineChart) {

        // ── Données ────────────────────────────────────────────────
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 2f)); // Jan
        entries.add(new Entry(1, 4f)); // Fév
        entries.add(new Entry(2, 1f)); // Mar
        entries.add(new Entry(3, 5f)); // Avr
        entries.add(new Entry(4, 3f)); // Mai

        LineDataSet dataSet = new LineDataSet(entries, "Visites");

        // ── Courbe Bézier fluide ───────────────────────────────────
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.25f);

        // ── Style ligne : bleu premium ─────────────────────────────
        dataSet.setColor(Color.parseColor("#378ADD"));
        dataSet.setLineWidth(3f);

        // ── Point final mis en valeur ──────────────────────────────
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(3f);
        dataSet.setCircleColor(Color.parseColor("#185FA5"));
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setDrawCircleHole(true);

        // Masquer toutes les valeurs numériques sur la courbe
        dataSet.setDrawValues(false);

        // ── Remplissage dégradé sous la courbe ────────────────────
        if (getContext() != null) {
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{
                            Color.parseColor("#4D378ADD"),   // 30% opacité bleu
                            Color.parseColor("#00EEF5FB")    // transparent vers la couleur du fond
                    }
            );
            dataSet.setDrawFilled(true);
            dataSet.setFillDrawable(gradientDrawable);
        }

        LineData data = new LineData(dataSet);
        lineChart.setData(data);

        // ── Nettoyage complet de l'interface ──────────────────────
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        // Axe X : masqué (les mois sont dans le XML)
        lineChart.getXAxis().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // Axes Y : masqués
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        // Désactiver toutes les interactions (pour ne pas bloquer le scroll)
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        // Fond transparent (la carte CardView fournit le blanc)
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);

        // Marges internes nulles pour que la courbe soit flush
        lineChart.setExtraOffsets(16f, 8f, 16f, 4f);

        // ── Animation d'entrée verticale ──────────────────────────
        lineChart.animateY(900);
    }
}