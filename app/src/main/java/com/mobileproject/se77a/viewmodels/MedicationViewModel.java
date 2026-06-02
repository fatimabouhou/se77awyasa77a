package com.mobileproject.se77a.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.repository.MedicationRepository;

import java.util.List;

public class MedicationViewModel extends AndroidViewModel {

    private final MedicationRepository repository;

    // ── LiveData the Fragment observes ─────────────────────────────────────
    private final LiveData<List<Medication>> allMedications;
    private final LiveData<List<Medication>> activeMedications;
    private final LiveData<Integer>          activeMedicationCount;
    private final LiveData<Integer>          takenTodayCount;

    public MedicationViewModel(@NonNull Application application) {
        super(application);
        repository            = new MedicationRepository(application);
        allMedications        = repository.getAllMedications();
        activeMedications     = repository.getActiveMedications();
        activeMedicationCount = repository.getActiveMedicationCount();
        takenTodayCount       = repository.getTakenTodayCount();
    }

    // ── Exposed LiveData ───────────────────────────────────────────────────
    public LiveData<List<Medication>> getAllMedications()    { return allMedications; }
    public LiveData<List<Medication>> getActiveMedications() { return activeMedications; }
    public LiveData<Integer> getActiveMedicationCount()      { return activeMedicationCount; }
    public LiveData<Integer> getTakenTodayCount()            { return takenTodayCount; }

    // ── Actions ────────────────────────────────────────────────────────────
    public void insert(Medication medication)  { repository.insert(medication); }
    public void update(Medication medication)  { repository.update(medication); }
    public void delete(Medication medication)  { repository.delete(medication); }

    public void markAsTaken(int medicationId, String takenTimes, boolean takenToday) {
        repository.updateTakenTimes(medicationId, takenTimes, takenToday);
    }    public void resetAllTakenToday()          { repository.resetAllTakenToday(); }
}