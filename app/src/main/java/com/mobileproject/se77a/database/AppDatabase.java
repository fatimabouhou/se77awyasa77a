package com.mobileproject.se77a.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mobileproject.se77a.database.dao.MedicationDao;
import com.mobileproject.se77a.database.dao.UserDao;
import com.mobileproject.se77a.database.dao.DoctorDao;
import com.mobileproject.se77a.database.dao.TimeSlotDao;
import com.mobileproject.se77a.database.dao.AppointmentDao;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.database.entities.Doctor;
import com.mobileproject.se77a.database.entities.TimeSlot;
import com.mobileproject.se77a.database.entities.Appointment;
import com.mobileproject.se77a.models.User;

@Database(
        entities = {
                User.class,
                Medication.class,
                Doctor.class,
                TimeSlot.class,
                Appointment.class
        },
        version = 6,              // MIGRATION : On passe de 5 à 6 pour intégrer l'âge, le poids, etc.
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao        userDao();
    public abstract MedicationDao  medicationDao();
    public abstract DoctorDao      doctorDao();
    public abstract TimeSlotDao    timeSlotDao();
    public abstract AppointmentDao appointmentDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "healthtracker_room.db"
                    )
                    .fallbackToDestructiveMigration()  // Grâce à cette ligne, Room va recréer la table proprement en version 6 !
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}