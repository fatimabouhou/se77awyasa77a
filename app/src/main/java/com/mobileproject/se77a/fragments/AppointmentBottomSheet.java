package com.mobileproject.se77a.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.adapters.DoctorAdapter;
import com.mobileproject.se77a.adapters.SlotAdapter;
import com.mobileproject.se77a.adapters.StringAdapter;
import com.mobileproject.se77a.database.entities.Doctor;
import com.mobileproject.se77a.database.entities.TimeSlot;
import com.mobileproject.se77a.repository.AppointmentRepository;

public class AppointmentBottomSheet extends BottomSheetDialogFragment {

    private final AppointmentRepository repo;
    private Doctor   selectedDoctor;
    private String   selectedSpecialty;
    private int      step = 1;

    private TextView   tvTitle;
    private RecyclerView recycler;
    private Button     btnBack;

    public AppointmentBottomSheet(AppointmentRepository repo) {
        this.repo = repo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_appointment, container, false);

        tvTitle  = view.findViewById(R.id.tv_sheet_title);
        recycler = view.findViewById(R.id.rv_appointment);
        btnBack  = view.findViewById(R.id.btn_back);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        btnBack.setOnClickListener(v -> {
            if (step > 1) {
                step--;
                loadStep();
            } else {
                dismiss();
            }
        });

        loadStep();
        return view;
    }

    private void loadStep() {
        switch (step) {
            case 1: loadSpecialties(); break;
            case 2: loadDoctorsBySpecialty(selectedSpecialty); break;
            case 3: loadSlots(); break;
        }
    }

    private void loadSpecialties() {
        tvTitle.setText("Choisissez une spécialité");
        repo.getAllSpecialties().observe(getViewLifecycleOwner(), specialties -> {
            if (specialties == null) return;
            recycler.setAdapter(new StringAdapter(specialties, specialty -> {
                selectedSpecialty = specialty;
                step = 2;
                loadDoctorsBySpecialty(specialty);
            }));
        });
    }

    private void loadDoctorsBySpecialty(String specialty) {
        if (specialty == null) { loadSpecialties(); return; }
        tvTitle.setText("Médecins — " + specialty);
        repo.getDoctorsBySpecialty(specialty).observe(getViewLifecycleOwner(), doctors -> {
            if (doctors == null) return;
            recycler.setAdapter(new DoctorAdapter(doctors, doctor -> {
                selectedDoctor = doctor;
                step = 3;
                loadSlots();
            }));
        });
    }

    private void loadSlots() {
        if (selectedDoctor == null) { step = 1; loadStep(); return; }
        tvTitle.setText(selectedDoctor.name + " — Créneaux");
        repo.getAvailableSlots(selectedDoctor.id).observe(getViewLifecycleOwner(), slots -> {
            if (slots == null) return;
            if (slots.isEmpty()) {
                tvTitle.setText("Aucun créneau disponible");
                recycler.setAdapter(null);
                return;
            }
            recycler.setAdapter(new SlotAdapter(slots, this::showConfirmation));
        });
    }

    private void showConfirmation(TimeSlot slot) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmer le rendez-vous ?")
                .setMessage(
                        "👨‍⚕️ " + selectedDoctor.name    + "\n" +
                                "🏥 "  + selectedDoctor.specialty + "\n" +
                                "📍 "  + selectedDoctor.address   + "\n" +
                                "📅 "  + slot.date + " à " + slot.time
                )
                .setPositiveButton("Confirmer", (d, w) ->
                        repo.confirmAppointment(selectedDoctor, slot, () -> {
                            Toast.makeText(getContext(),
                                    "✅ RDV confirmé — " + slot.date + " à " + slot.time,
                                    Toast.LENGTH_SHORT).show();
                            dismiss();
                        })
                )
                .setNegativeButton("Annuler", null)
                .show();
    }
}