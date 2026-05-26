package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.mobileproject.se77a.database.entities.Medication;
import java.util.List;

@Dao
public interface MedicationDao {

    @Insert
    void insert(Medication medication);

    @Update
    void update(Medication medication);

    @Delete
    void delete(Medication medication);

    @Query("SELECT * FROM medications ORDER BY name ASC")
    LiveData<List<Medication>> getAllMedications();

    @Query("SELECT * FROM medications WHERE isActive = 1")
    LiveData<List<Medication>> getActiveMedications();

    @Query("SELECT * FROM medications WHERE id = :id")
    LiveData<Medication> getMedicationById(int id);
}