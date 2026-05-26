package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.mobileproject.se77a.database.entities.Appointment;
import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert
    void insert(Appointment appointment);

    @Update
    void update(Appointment appointment);

    @Delete
    void delete(Appointment appointment);

    @Query("SELECT * FROM appointments ORDER BY date ASC, time ASC")
    LiveData<List<Appointment>> getAllAppointments();

    @Query("SELECT * FROM appointments WHERE id = :id")
    LiveData<Appointment> getAppointmentById(int id);

    @Query("SELECT * FROM appointments WHERE date >= :today ORDER BY date ASC LIMIT 1")
    LiveData<Appointment> getNextAppointment(String today);
}