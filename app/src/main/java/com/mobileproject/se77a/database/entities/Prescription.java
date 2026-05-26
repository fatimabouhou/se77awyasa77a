package com.mobileproject.se77a.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "prescriptions")
public class Prescription {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int appointmentId;
    public String imagePath;
    public String date;
    public String notes;

    public Prescription(int appointmentId, String imagePath, String date, String notes) {
        this.appointmentId = appointmentId;
        this.imagePath = imagePath;
        this.date = date;
        this.notes = notes;
    }
}