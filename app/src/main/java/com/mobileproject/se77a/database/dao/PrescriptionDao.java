package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.mobileproject.se77a.database.entities.Prescription;
import java.util.List;

@Dao
public interface PrescriptionDao {

    @Insert
    void insert(Prescription prescription);

    @Update
    void update(Prescription prescription);

    @Delete
    void delete(Prescription prescription);

    @Query("SELECT * FROM prescriptions ORDER BY date DESC")
    LiveData<List<Prescription>> getAllPrescriptions();

    @Query("SELECT * FROM prescriptions WHERE appointmentId = :appointmentId")
    LiveData<List<Prescription>> getPrescriptionsByAppointment(int appointmentId);

    @Query("SELECT COUNT(*) FROM prescriptions")   // ← NOUVEAU
    LiveData<Integer> getCount();
}