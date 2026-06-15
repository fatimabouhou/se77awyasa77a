package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mobileproject.se77a.database.entities.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert
    long insert(Appointment appointment);

    @Query("SELECT * FROM appointments WHERE status = 'UPCOMING' ORDER BY date, time")
    LiveData<List<Appointment>> getUpcoming();

    @Query("SELECT * FROM appointments ORDER BY date DESC")
    LiveData<List<Appointment>> getAll();

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    void updateStatus(int id, String status);

    @Query("SELECT * FROM appointments WHERE status = 'UPCOMING' " +
            "ORDER BY date, time LIMIT 1")
    LiveData<Appointment> getNextAppointment();

    @Query("SELECT COUNT(*) FROM appointments WHERE status = 'UPCOMING' AND date LIKE :monthPrefix || '%'")
    LiveData<Integer> getCountForMonth(String monthPrefix);
}