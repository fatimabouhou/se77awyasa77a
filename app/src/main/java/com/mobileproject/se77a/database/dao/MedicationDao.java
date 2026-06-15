package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.mobileproject.se77a.database.entities.Medication;
import java.util.List;

@Dao
public interface MedicationDao {

    // ── Writes ─────────────────────────────────────────────────────────────
    @Insert
    long insert(Medication medication);

    @Update
    void update(Medication medication);

    @Delete
    void delete(Medication medication);

    // ── Reads (LiveData — observed by ViewModel) ───────────────────────────
    @Query("SELECT * FROM medications ORDER BY name ASC")
    LiveData<List<Medication>> getAllMedications();

    @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY reminderTime ASC")
    LiveData<List<Medication>> getActiveMedications();

    @Query("SELECT * FROM medications WHERE id = :id")
    LiveData<Medication> getMedicationById(int id);

    @Query("SELECT * FROM medications WHERE id = :id")
    Medication getMedicationByIdSync(int id);

    // ── takenToday helpers ─────────────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM medications")
    int countMedications();
    // Marque un médicament comme entièrement pris (toutes ses doses)
    @Query("UPDATE medications SET takenToday = 1, takenTimes = reminderTime WHERE id = :id")
    void markAsTakenFull(int id);

    // Mise à jour dose par dose (depuis FragmentTracking)
    @Query("UPDATE medications SET takenTimes = :takenTimes, takenToday = :takenToday WHERE id = :id")
    void updateTakenTimes(int id, String takenTimes, boolean takenToday);

    // Reset quotidien : remet tout à zéro
    @Query("UPDATE medications SET takenToday = 0, takenTimes = ''")
    void resetAllTakenToday();

    @Query("SELECT COUNT(*) FROM medications WHERE isActive = 1")
    LiveData<Integer> getActiveMedicationCount();

    @Query("SELECT COUNT(*) FROM medications WHERE takenToday = 1")
    LiveData<Integer> getTakenTodayCount();
}