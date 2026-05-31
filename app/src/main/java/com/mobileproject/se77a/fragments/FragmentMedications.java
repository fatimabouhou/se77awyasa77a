package com.mobileproject.se77a.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.adapters.MedicationAdapter;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.viewmodels.MedicationViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentMedications extends Fragment
        implements MedicationAdapter.OnMedicationClickListener {

    // ── Views ──────────────────────────────────────────────────────────────
    private RecyclerView         rvMedications;
    private LinearLayout         llEmptyState;
    private TextView             tvRemainingCount, tvProgressLabel;
    private ProgressBar          progressToday;
    private TextView             tabAll, tabActive, tabTaken;
    private FloatingActionButton fabAdd;

    // ── Architecture components ────────────────────────────────────────────
    private MedicationAdapter  adapter;
    private MedicationViewModel viewModel;

    // ── Local state ────────────────────────────────────────────────────────
    private final List<Medication> allMedicationsList = new ArrayList<>();
    private String currentFilter = "all";
    private final List<String> selectedTimes = new ArrayList<>();

    // ── Header counters (kept in sync by ViewModel LiveData) ───────────────
    private int totalActive = 0;
    private int totalTaken  = 0;

    // =========================================================
    // LIFECYCLE
    // =========================================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupRecyclerView();
        setupViewModel();
        setupFilterTabs();
        fabAdd.setOnClickListener(v -> showAddMedicationSheet());
    }

    // =========================================================
    // SETUP
    // =========================================================
    private void bindViews(View root) {
        rvMedications    = root.findViewById(R.id.rv_medications);
        llEmptyState     = root.findViewById(R.id.ll_empty_state);
        tvRemainingCount = root.findViewById(R.id.tv_remaining_count);
        tvProgressLabel  = root.findViewById(R.id.tv_progress_label);
        progressToday    = root.findViewById(R.id.progress_today);
        tabAll           = root.findViewById(R.id.tab_all);
        tabActive        = root.findViewById(R.id.tab_active);
        tabTaken         = root.findViewById(R.id.tab_taken);
        fabAdd           = root.findViewById(R.id.fab_add_medication);
    }

    private void setupRecyclerView() {
        adapter = new MedicationAdapter(this);
        rvMedications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMedications.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MedicationViewModel.class);

        // Main list — drives the RecyclerView
        viewModel.getAllMedications().observe(getViewLifecycleOwner(), medications -> {
            allMedicationsList.clear();
            allMedicationsList.addAll(medications);
            applyFilter(currentFilter);
        });

        // Active count LiveData — for the header "remaining" label
        viewModel.getActiveMedicationCount().observe(getViewLifecycleOwner(), count -> {
            totalActive = count != null ? count : 0;
            updateHeader();
        });

        // Taken today count LiveData — drives the progress bar
        viewModel.getTakenTodayCount().observe(getViewLifecycleOwner(), count -> {
            totalTaken = count != null ? count : 0;
            updateHeader();
        });
    }

    // =========================================================
    // FILTER TABS
    // =========================================================
    private void setupFilterTabs() {
        tabAll.setOnClickListener(v    -> selectTab("all"));
        tabActive.setOnClickListener(v -> selectTab("active"));
        tabTaken.setOnClickListener(v  -> selectTab("taken"));
    }

    private void selectTab(String filter) {
        currentFilter = filter;

        setTabStyle(tabAll,    "all".equals(filter));
        setTabStyle(tabActive, "active".equals(filter));
        setTabStyle(tabTaken,  "taken".equals(filter));

        applyFilter(filter);
    }

    private void setTabStyle(TextView tab, boolean active) {
        tab.setBackgroundResource(active ? R.drawable.bg_tab_active : R.drawable.bg_tab_inactive);
        tab.setTextColor(requireContext().getColor(active ? R.color.white : R.color.blue_light_1));
    }

    private void applyFilter(String filter) {
        List<Medication> filtered = new ArrayList<>();
        for (Medication m : allMedicationsList) {
            switch (filter) {
                case "active": if (m.isActive && !m.takenToday) filtered.add(m); break;
                case "taken":  if (m.takenToday)                filtered.add(m); break;
                default:       filtered.add(m);
            }
        }
        adapter.setMedications(filtered);
        toggleEmptyState(filtered.isEmpty());
    }

    // =========================================================
    // HEADER (driven by ViewModel LiveData counters)
    // =========================================================
    private void updateHeader() {
        int remaining = totalActive - totalTaken;
        if (remaining < 0) remaining = 0;

        tvRemainingCount.setText(String.valueOf(remaining));
        tvProgressLabel.setText(totalTaken + "/" + totalActive);

        int progress = totalActive == 0 ? 0
                : (int) ((totalTaken / (float) totalActive) * 100);
        progressToday.setProgress(progress);
    }

    private void toggleEmptyState(boolean isEmpty) {
        rvMedications.setVisibility(isEmpty ? View.GONE    : View.VISIBLE);
        llEmptyState.setVisibility(isEmpty  ? View.VISIBLE : View.GONE);
    }

    // =========================================================
    // ADAPTER CALLBACKS (OnMedicationClickListener)
    // =========================================================
    @Override
    public void onMedicationClick(Medication medication) {
        // TODO: open detail / edit screen
    }

    @Override
    public void onToggleActive(Medication medication, boolean isActive) {
        medication.isActive = isActive;
        viewModel.update(medication);
    }

    @Override
    public void onMarkTaken(Medication medication) {
        viewModel.markAsTaken(medication.id);
        Toast.makeText(getContext(),
                medication.name + " marqué comme pris ✓", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteMedication(Medication medication) {
        viewModel.delete(medication);
        Toast.makeText(getContext(),
                medication.name + " supprimé", Toast.LENGTH_SHORT).show();
    }

    // =========================================================
    // ADD MEDICATION — BOTTOM SHEET
    // =========================================================
    private void showAddMedicationSheet() {
        selectedTimes.clear();

        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_medication, null);
        sheet.setContentView(v);

        // --- Keyboard and Scroll management ---
        // Ensure the sheet is draggable/scrollable and expands fully
        if (v.getParent() != null) {
            View parent = (View) v.getParent();
            parent.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
        
        // Handle background tap to hide keyboard
        v.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        // Bind dialog views
        TextInputEditText    etName     = v.findViewById(R.id.et_med_name);
        TextInputEditText    etDosage   = v.findViewById(R.id.et_dosage);
        TextInputLayout      tilName    = v.findViewById(R.id.til_med_name);
        TextInputLayout      tilDosage  = v.findViewById(R.id.til_dosage);
        AutoCompleteTextView actvFreq   = v.findViewById(R.id.actv_frequency);
        ChipGroup            chipTimes  = v.findViewById(R.id.chip_group_times);
        ChipGroup            chipType   = v.findViewById(R.id.chip_group_type);
        MaterialButton       btnAddTime = v.findViewById(R.id.btn_add_time);
        MaterialButton       btnSave    = v.findViewById(R.id.btn_save_medication);
        MaterialButton       btnCancel  = v.findViewById(R.id.btn_cancel);
        SwitchCompat         swReminder = v.findViewById(R.id.switch_enable_reminder);

        // Frequency dropdown
        String[] freqs = {"1×/jour","2×/jour","3×/jour","4×/jour",
                "Toutes les 8h","Toutes les 12h","Si besoin"};
        actvFreq.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, freqs));
        actvFreq.setOnClickListener(x -> actvFreq.showDropDown());

        // Time picker → chip
        btnAddTime.setOnClickListener(x ->
                new TimePickerDialog(requireContext(), (tp, h, m) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", h, m);
                    if (!selectedTimes.contains(time)) {
                        selectedTimes.add(time);
                        addTimeChip(chipTimes, time);
                    }
                }, 8, 0, true).show()
        );

        btnCancel.setOnClickListener(x -> sheet.dismiss());

        // Save → insert into Room via ViewModel
        btnSave.setOnClickListener(x -> {
            String name  = etName.getText()   != null ? etName.getText().toString().trim()   : "";
            String dosage= etDosage.getText() != null ? etDosage.getText().toString().trim() : "";
            String freq  = actvFreq.getText().toString().trim();

            // Validate
            boolean valid = true;
            if (name.isEmpty())  { tilName.setError("Champ requis");   valid = false; } else tilName.setError(null);
            if (dosage.isEmpty()){ tilDosage.setError("Champ requis"); valid = false; } else tilDosage.setError(null);
            if (freq.isEmpty())  {
                Toast.makeText(requireContext(), "Choisissez une fréquence", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if (!valid) return;

            String firstTime = selectedTimes.isEmpty() ? "08:00" : selectedTimes.get(0);
            String type      = resolveType(chipType);

            Medication newMed = new Medication(name, dosage, freq, "", "", firstTime, type);
            newMed.isActive = swReminder.isChecked();

            viewModel.insert(newMed);  // ← persisted to Room, LiveData auto-refreshes the list
            sheet.dismiss();
            Toast.makeText(getContext(), "Médicament ajouté ✓", Toast.LENGTH_SHORT).show();
        });

        sheet.show();
    }

    private void addTimeChip(ChipGroup group, String time) {
        Chip chip = new Chip(requireContext());
        chip.setText(time);
        chip.setCloseIconVisible(true);
        chip.setTextColor(requireContext().getColor(R.color.white));
        chip.setChipBackgroundColorResource(R.color.blue_dark_1);
        chip.setChipStrokeColorResource(R.color.blue_medium);
        chip.setChipStrokeWidth(1f);
        chip.setCloseIconTintResource(R.color.blue_light_1);
        chip.setOnCloseIconClickListener(c -> {
            selectedTimes.remove(time);
            group.removeView(chip);
        });
        group.addView(chip);
    }

    private String resolveType(ChipGroup chipType) {
        int id = chipType.getCheckedChipId();
        if (id == R.id.chip_syrup)     return "syrup";
        if (id == R.id.chip_injection) return "injection";
        if (id == R.id.chip_drops)     return "drops";
        return "tablet";
    }
}