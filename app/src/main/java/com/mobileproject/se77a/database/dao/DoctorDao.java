package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mobileproject.se77a.database.entities.Doctor;

import java.util.List;

@Dao
public interface DoctorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Doctor> doctors);

    @Query("SELECT * FROM doctors ORDER BY specialty, name")
    LiveData<List<Doctor>> getAllDoctors();

    @Query("SELECT DISTINCT specialty FROM doctors ORDER BY specialty")
    LiveData<List<String>> getAllSpecialties();

    @Query("SELECT * FROM doctors WHERE specialty = :specialty ORDER BY name")
    LiveData<List<Doctor>> getDoctorsBySpecialty(String specialty);

    @Query("SELECT COUNT(*) FROM doctors")
    int getCount();
}