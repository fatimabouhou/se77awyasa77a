package com.mobileproject.se77a.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "analyses")
public class Analysis {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String imagePath;
    public String date;
    public String notes;

    public Analysis(int id, String imagePath, String date, String notes) {
        this.id        = id;
        this.imagePath = imagePath;
        this.date      = date;
        this.notes     = notes;
    }
}