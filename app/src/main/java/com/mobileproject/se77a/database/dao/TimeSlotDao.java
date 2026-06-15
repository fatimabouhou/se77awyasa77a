package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mobileproject.se77a.database.entities.TimeSlot;

import java.util.List;

@Dao
public interface TimeSlotDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<TimeSlot> slots);

    @Query("SELECT * FROM time_slots WHERE doctorId = :doctorId " +
            "AND isAvailable = 1 AND date >= :today ORDER BY date, time")
    LiveData<List<TimeSlot>> getAvailableSlots(int doctorId, String today);

    @Query("UPDATE time_slots SET isAvailable = 0 WHERE id = :slotId")
    void markUnavailable(int slotId);

    @Query("SELECT COUNT(*) FROM time_slots WHERE doctorId = :doctorId")
    int countForDoctor(int doctorId);
}