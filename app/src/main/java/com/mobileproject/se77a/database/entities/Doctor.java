package com.mobileproject.se77a.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doctors")
public class Doctor {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;
    public String specialty;
    public String address;
    public String phone;

    public Doctor(@NonNull String name, String specialty,
                  String address, String phone) {
        this.name      = name;
        this.specialty = specialty;
        this.address   = address;
        this.phone     = phone;
    }
}