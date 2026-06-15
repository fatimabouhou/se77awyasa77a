package com.mobileproject.se77a.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "time_slots")
public class TimeSlot {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int     doctorId;
    public String  date;          // "2026-06-17"
    public String  time;          // "09:00"
    public boolean isAvailable;

    public TimeSlot(int doctorId, String date, String time) {
        this.doctorId    = doctorId;
        this.date        = date;
        this.time        = time;
        this.isAvailable = true;
    }
}