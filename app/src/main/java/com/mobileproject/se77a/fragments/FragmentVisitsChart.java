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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mobileproject.se77a.R;

import java.util.ArrayList;

public class FragmentVisitsChart extends Fragment {

    public FragmentVisitsChart() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visits_chart, container, false);

        LineChart lineChart = view.findViewById(R.id.lineChart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 2f)); // Jan
        entries.add(new Entry(1, 4f)); // Fév
        entries.add(new Entry(2, 1f)); // Mar
        entries.add(new Entry(3, 5f)); // Avr
        entries.add(new Entry(4, 3f)); // Mai

        LineDataSet dataSet = new LineDataSet(entries, "Visites");

        // 1. Fluidité absolue de la courbe (Bézier)
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.25f);

        // 2. Style de la ligne principale (Bleu Tech Premium)
        dataSet.setColor(Color.parseColor("#2563EB"));
        dataSet.setLineWidth(3.5f);

        // 3. Élimination des points basiques sur chaque étape (Style épuré)
        dataSet.setDrawCircles(false);

        // 4. Masquer les valeurs numériques sur les points (Évite la surcharge visuelle)
        dataSet.setDrawValues(false);

        // 5. Remplissage sous la courbe avec un dégradé fluide vers la transparence
        if (getContext() != null) {
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{Color.parseColor("#403B82F6"), Color.parseColor("#00FFFFFF")}
            );
            dataSet.setDrawFilled(true);
            dataSet.setFillDrawable(gradientDrawable);
        } else {
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(Color.parseColor("#3B82F6"));
            dataSet.setFillAlpha(30);
        }

        LineData data = new LineData(dataSet);
        lineChart.setData(data);

        // ==========================================
        // NETTOYAGE COMPLET DE L'INTERFACE GRAPHIQUE
        // ==========================================
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        // On cache tous les axes et grilles pour que la vague fusionne avec la carte blanche
        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        // On désactive les zooms et interactions pour ne pas bloquer le scroll de l'écran
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);

        // Animation d'entrée verticale fluide
        lineChart.animateY(800);

        return view;
    }
}