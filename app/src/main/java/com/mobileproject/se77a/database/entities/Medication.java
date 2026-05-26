package com.mobileproject.se77a.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medications")
public class Medication {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String dosage;
    public String frequency;
    public String startDate;
    public String endDate;
    public String reminderTime;
    public boolean isActive;

    public Medication(String name, String dosage, String frequency,
                      String startDate, String endDate, String reminderTime) {
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reminderTime = reminderTime;
        this.isActive = true;
    }
}