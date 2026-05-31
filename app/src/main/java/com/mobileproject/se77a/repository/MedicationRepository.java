package com.mobileproject.se77a.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.dao.MedicationDao;
import com.mobileproject.se77a.database.entities.Medication;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicationRepository {

    private final MedicationDao    medicationDao;
    private final ExecutorService  executor;

    // LiveData exposed to ViewModel
    private final LiveData<List<Medication>> allMedications;
    private final LiveData<List<Medication>> activeMedications;
    private final LiveData<Integer>          activeMedicationCount;
    private final LiveData<Integer>          takenTodayCount;

    public MedicationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        medicationDao         = db.medicationDao();
        executor              = Executors.newSingleThreadExecutor();

        allMedications        = medicationDao.getAllMedications();
        activeMedications     = medicationDao.getActiveMedications();
        activeMedicationCount = medicationDao.getActiveMedicationCount();
        takenTodayCount       = medicationDao.getTakenTodayCount();
    }

    // ── Getters (LiveData — no thread needed, Room handles it) ─────────────
    public LiveData<List<Medication>> getAllMedications()    { return allMedications; }
    public LiveData<List<Medication>> getActiveMedications() { return activeMedications; }
    public LiveData<Integer> getActiveMedicationCount()      { return activeMedicationCount; }
    public LiveData<Integer> getTakenTodayCount()            { return takenTodayCount; }

    // ── Writes (must run off the main thread) ──────────────────────────────
    public void insert(Medication medication) {
        executor.execute(() -> medicationDao.insert(medication));
    }

    public void update(Medication medication) {
        executor.execute(() -> medicationDao.update(medication));
    }

    public void delete(Medication medication) {
        executor.execute(() -> medicationDao.delete(medication));
    }

    public void markAsTaken(int medicationId) {
        executor.execute(() -> medicationDao.markAsTaken(medicationId));
    }

    public void resetAllTakenToday() {
        executor.execute(() -> medicationDao.resetAllTakenToday());
    }
}