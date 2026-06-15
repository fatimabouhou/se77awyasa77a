package com.mobileproject.se77a.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.dao.MedicationDao;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.utils.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicationRepository {

    private final MedicationDao   medicationDao;
    private final ExecutorService executor;
    private final ReminderManager reminderManager;
    private final Application     application;

    // LiveData exposed to ViewModel
    private final LiveData<List<Medication>> allMedications;
    private final LiveData<List<Medication>> activeMedications;
    private final LiveData<Integer>          activeMedicationCount;
    private final LiveData<Integer>          takenTodayCount;

    public MedicationRepository(Application application) {
        this.application          = application;
        AppDatabase db            = AppDatabase.getInstance(application);
        medicationDao             = db.medicationDao();
        executor                  = Executors.newSingleThreadExecutor();
        reminderManager           = new ReminderManager(application);

        allMedications            = medicationDao.getAllMedications();
        activeMedications         = medicationDao.getActiveMedications();
        activeMedicationCount     = medicationDao.getActiveMedicationCount();
        takenTodayCount           = medicationDao.getTakenTodayCount();
    }

    // ── Getters (LiveData) ─────────────────────────────────────────────────
    public LiveData<List<Medication>> getAllMedications()    { return allMedications; }
    public LiveData<List<Medication>> getActiveMedications() { return activeMedications; }
    public LiveData<Integer> getActiveMedicationCount()      { return activeMedicationCount; }
    public LiveData<Integer> getTakenTodayCount()            { return takenTodayCount; }

    // ── Writes ─────────────────────────────────────────────────────────────
    public void insert(Medication medication) {
        executor.execute(() -> {
            long id = medicationDao.insert(medication);
            medication.id = (int) id;
            if (medication.isActive) {
                reminderManager.scheduleMedicationAlarm(medication);
            }
        });
    }

    public void update(Medication medication) {
        executor.execute(() -> {
            medicationDao.update(medication);
            if (medication.isActive) {
                reminderManager.scheduleMedicationAlarm(medication);
            } else {
                reminderManager.cancelMedicationAlarms(medication);
            }
        });
    }

    public void delete(Medication medication) {
        executor.execute(() -> {
            medicationDao.delete(medication);
            reminderManager.cancelMedicationAlarms(medication);
        });
    }

    // Marque toutes les doses d'un médicament comme prises
    public void markAsTaken(int medicationId) {
        executor.execute(() -> medicationDao.markAsTakenFull(medicationId));
    }

    // Mise à jour dose par dose (depuis FragmentTracking)
    public void updateTakenTimes(int medicationId, String takenTimes, boolean takenToday) {
        executor.execute(() ->
                medicationDao.updateTakenTimes(medicationId, takenTimes, takenToday)
        );
    }

    public void markNextDoseAsTaken(int medId) {
        executor.execute(() -> {
            Medication med = medicationDao.getMedicationByIdSync(medId);
            if (med == null) return;

            if (med.reminderTime == null || med.reminderTime.isEmpty()) {
                med.takenToday = true;
                medicationDao.update(med);
                return;
            }

            String[] allTimes = med.reminderTime.split(",");
            String currentTaken = (med.takenTimes != null) ? med.takenTimes : "";
            String[] takenArray = currentTaken.isEmpty() ? new String[0] : currentTaken.split(",");

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
                med.takenTimes = newTaken;
                if (newTaken.split(",").length >= allTimes.length) {
                    med.takenToday = true;
                }
                medicationDao.update(med);
            }
        });
    }

    // Reset quotidien automatique
    public void resetIfNewDay() {
        executor.execute(() -> {
            SharedPreferences prefs = application
                    .getSharedPreferences("med_prefs", Context.MODE_PRIVATE);

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());
            String lastReset = prefs.getString("last_reset_date", "");

            if (!today.equals(lastReset)) {
                medicationDao.resetAllTakenToday();
                prefs.edit().putString("last_reset_date", today).apply();
            }
        });
    }

    public void resetAllTakenToday() {
        executor.execute(() -> medicationDao.resetAllTakenToday());
    }
}