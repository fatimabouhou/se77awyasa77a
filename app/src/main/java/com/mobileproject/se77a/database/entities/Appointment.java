package com.mobileproject.se77a.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments")
public class Appointment {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String doctorName;
    public String specialty;
    public String date;
    public String time;
    public String phone;
    public String address;
    public String notes;

    public Appointment(String doctorName, String specialty, String date,
                       String time, String phone, String address, String notes) {
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.date = date;
        this.time = time;
        this.phone = phone;
        this.address = address;
        this.notes = notes;
    }
}