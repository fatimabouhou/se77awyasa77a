package com.mobileproject.se77a.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.adapters.MedicationAdapter;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.utils.NotificationHelper;
import com.mobileproject.se77a.utils.TimeUtils;
import com.mobileproject.se77a.viewmodels.MedicationViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FragmentMedications extends Fragment
        implements MedicationAdapter.OnMedicationClickListener {

    private RecyclerView         rvMedications;
    private LinearLayout         llEmptyState;
    private TextView             tvRemainingCount, tvProgressLabel;
    private ProgressBar          progressToday;
    private TextView             tabAll, tabActive, tabTaken;
    private FloatingActionButton fabAdd;

    private MedicationAdapter  adapter;
    private MedicationViewModel viewModel;

    private final List<Medication> allMedicationsList = new ArrayList<>();
    private String currentFilter = "all";
    private final List<String> selectedTimes = new ArrayList<>();

    private int totalActive = 0;
    private int totalTaken  = 0;

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

        // Sequential permission check to avoid Activity interruption
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                checkExactAlarmPermission();
            }
        } else {
            checkExactAlarmPermission();
        }
    }

    private void checkNotificationPermission() {
        // Method now empty as logic moved to onViewCreated for better flow
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(getContext(), "Notifications activées", Toast.LENGTH_SHORT).show();
                }
                // Check alarm permission after notification permission is handled
                checkExactAlarmPermission();
            });

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(getContext(), "Veuillez autoriser les alarmes pour recevoir les rappels", Toast.LENGTH_LONG).show();
            }
        }
    }

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

        viewModel.getAllMedications().observe(getViewLifecycleOwner(), medications -> {
            allMedicationsList.clear();
            allMedicationsList.addAll(medications);
            applyFilter(currentFilter);
        });

        viewModel.getActiveMedicationCount().observe(getViewLifecycleOwner(), count -> {
            totalActive = count != null ? count : 0;
            updateHeader();
        });

        viewModel.getTakenTodayCount().observe(getViewLifecycleOwner(), count -> {
            totalTaken = count != null ? count : 0;
            updateHeader();
        });
    }

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
        tab.setTextColor(requireContext().getColor(active ? R.color.white : R.color.blue_medium));
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

    private void updateHeader() {
        int count = Math.max(0, totalActive - totalTaken);
        tvRemainingCount.setText(String.valueOf(count));
        tvProgressLabel.setText(String.format(Locale.getDefault(), "%d/%d", totalTaken, totalActive));
        int progress = totalActive == 0 ? 0 : (int) ((totalTaken / (float) totalActive) * 100);
        progressToday.setProgress(progress);
    }

    private void toggleEmptyState(boolean isEmpty) {
        rvMedications.setVisibility(isEmpty ? View.GONE    : View.VISIBLE);
        llEmptyState.setVisibility(isEmpty  ? View.VISIBLE : View.GONE);
    }

    @Override public void onMedicationClick(Medication medication) {}
    @Override public void onToggleActive(Medication medication, boolean isActive) {
        medication.isActive = isActive;
        viewModel.update(medication);
    }
    @Override public void onMarkTaken(Medication medication) {
        // Arrêter la notification si elle est active
        NotificationHelper notificationHelper = new NotificationHelper(requireContext());
        notificationHelper.cancelNotification(medication.id);

        if (medication.reminderTime == null || medication.reminderTime.isEmpty()) {
            medication.takenToday = true;
            viewModel.update(medication);
            return;
        }

        String[] allTimes = medication.reminderTime.split(",");
        String currentTaken = (medication.takenTimes != null) ? medication.takenTimes : "";
        String[] takenArray = currentTaken.isEmpty() ? new String[0] : currentTaken.split(",");

        // Find the first time that hasn't been taken yet
        String doseToMark = "";
        for (String time : allTimes) {
            boolean alreadyTaken = false;
            for (String t : takenArray) {
                if (time.trim().equals(t.trim())) {
                    alreadyTaken = true;
                    break;
                }
            }
            if (!alreadyTaken) {
                doseToMark = time.trim();
                break;
            }
        }

        if (!doseToMark.isEmpty()) {
            String newTaken = currentTaken.isEmpty() ? doseToMark : currentTaken + "," + doseToMark;
            medication.takenTimes = newTaken;
            
            // Check if all doses for the day are now taken
            if (newTaken.split(",").length >= allTimes.length) {
                medication.takenToday = true;
            }
            
            viewModel.update(medication);
            Toast.makeText(getContext(), medication.name + " (" + doseToMark + ") marqué pris ✓", Toast.LENGTH_SHORT).show();
        }
    }
    @Override public void onDeleteMedication(Medication medication) {
        viewModel.delete(medication);
        Toast.makeText(getContext(), medication.name + " supprimé", Toast.LENGTH_SHORT).show();
    }

    private void showAddMedicationSheet() {
        selectedTimes.clear();
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_medication, (ViewGroup) getView(), false);
        sheet.setContentView(v);

        // Hide keyboard when tapping outside an EditText
        v.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        // Expand BottomSheet fully
        sheet.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        TextInputEditText etName = v.findViewById(R.id.et_med_name);
        TextInputEditText etDosage = v.findViewById(R.id.et_dosage);
        TextInputEditText etDateStart = v.findViewById(R.id.et_date_start);
        TextInputEditText etDateEnd = v.findViewById(R.id.et_date_end);
        AutoCompleteTextView actvFreq = v.findViewById(R.id.actv_frequency);
        ChipGroup chipTimes = v.findViewById(R.id.chip_group_times);
        ChipGroup chipType = v.findViewById(R.id.chip_group_type);
        SwitchCompat swReminder = v.findViewById(R.id.switch_enable_reminder);
        MaterialButton btnSave = v.findViewById(R.id.btn_save_medication);

        // Set default selection
        chipType.check(R.id.chip_tablet);

        // --- Date Pickers ---
        etDateStart.setOnClickListener(x -> showDatePicker(etDateStart));
        etDateEnd.setOnClickListener(x -> showDatePicker(etDateEnd));

        // --- Logic to enable/disable the Add button ---
        Runnable validator = () -> {
            boolean isValid = etName.getText() != null && !etName.getText().toString().trim().isEmpty()
                    && etDosage.getText() != null && !etDosage.getText().toString().trim().isEmpty()
                    && !actvFreq.getText().toString().trim().isEmpty()
                    && !selectedTimes.isEmpty();
            btnSave.setEnabled(isValid);
            btnSave.setAlpha(isValid ? 1.0f : 0.5f);
        };

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validator.run();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        etName.addTextChangedListener(watcher);
        etDosage.addTextChangedListener(watcher);
        actvFreq.addTextChangedListener(watcher);

        String[] freqs = {"1×/jour","2×/jour","3×/jour","Toutes les 8h","Toutes les 12h"};
        actvFreq.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, freqs));
        actvFreq.setOnClickListener(x -> actvFreq.showDropDown());

        v.findViewById(R.id.btn_add_time).setOnClickListener(x ->
                new TimePickerDialog(requireContext(), (tp, h, m) -> {
                    String time24h = String.format(Locale.getDefault(), "%02d:%02d", h, m);
                    if (!selectedTimes.contains(time24h)) {
                        selectedTimes.add(time24h);
                        addTimeChip(chipTimes, time24h, validator);
                        validator.run();
                    }
                }, 8, 0, android.text.format.DateFormat.is24HourFormat(requireContext())).show()
        );

        v.findViewById(R.id.btn_cancel).setOnClickListener(x -> sheet.dismiss());

        v.findViewById(R.id.btn_save_medication).setOnClickListener(x -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String dosage = etDosage.getText() != null ? etDosage.getText().toString().trim() : "";
            String freq = actvFreq.getText().toString().trim();
            String dateS = etDateStart.getText() != null ? etDateStart.getText().toString() : "";
            String dateE = etDateEnd.getText() != null ? etDateEnd.getText().toString() : "";

            StringBuilder timeBuilder = new StringBuilder();
            if (selectedTimes.isEmpty()) {
                timeBuilder.append("08:00");
            } else {
                for (int i = 0; i < selectedTimes.size(); i++) {
                    timeBuilder.append(selectedTimes.get(i));
                    if (i < selectedTimes.size() - 1) timeBuilder.append(",");
                }
            }
            String time = timeBuilder.toString();
            Medication newMed = new Medication(name, dosage, freq, dateS, dateE, time, resolveType(chipType));
            newMed.isActive = swReminder.isChecked();
            viewModel.insert(newMed);
            sheet.dismiss();
            Toast.makeText(getContext(), "Médicament ajouté ✓", Toast.LENGTH_SHORT).show();
        });

        sheet.show();
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(y, m, d);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editText.setText(format.format(calendar.getTime()));
        }, year, month, day).show();
    }

    private void addTimeChip(ChipGroup group, String time24h, Runnable onRemove) {
        Chip chip = new Chip(requireContext());
        chip.setText(TimeUtils.formatTimeForDisplay(requireContext(), time24h));
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(c -> {
            selectedTimes.remove(time24h);
            group.removeView(chip);
            if (onRemove != null) onRemove.run();
        });
        group.addView(chip);
    }

    private String resolveType(ChipGroup chipType) {
        int id = chipType.getCheckedChipId();
        if (id == R.id.chip_syrup) return "syrup";
        if (id == R.id.chip_injection) return "injection";
        if (id == R.id.chip_drops) return "drops";
        if (id == R.id.chip_tablet) return "tablet";
        return "tablet"; // fallback
    }
}
