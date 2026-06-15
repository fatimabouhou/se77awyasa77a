package com.mobileproject.se77a.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable; // Import nécessaire

@Entity(tableName = "appointments")
public class Appointment implements Serializable { // Modification ici

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int    doctorId;
    public int    timeSlotId;
    public String doctorName;
    public String specialty;
    public String address;
    public String phone;
    public String date;
    public String time;
    public String status;      // "UPCOMING" | "DONE" | "CANCELLED"
    public long   createdAt;
}