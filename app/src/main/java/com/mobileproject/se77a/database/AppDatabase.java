package com.mobileproject.se77a.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.mobileproject.se77a.database.dao.*;
import com.mobileproject.se77a.database.entities.*;

@Database(
        entities = {Appointment.class, Medication.class, Prescription.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract AppointmentDao appointmentDao();
    public abstract MedicationDao medicationDao();
    public abstract PrescriptionDao prescriptionDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "healthtracker_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}