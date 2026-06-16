package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mobileproject.se77a.database.entities.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert
    long insert(Appointment appointment);

    @Query("SELECT * FROM appointments WHERE status = 'UPCOMING' ORDER BY date, time")
    LiveData<List<Appointment>> getUpcoming();

    @Query("SELECT * FROM appointments ORDER BY date DESC")
    LiveData<List<Appointment>> getAll();

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    void updateStatus(int id, String status);

    @Query("SELECT * FROM appointments WHERE status = 'UPCOMING' " +
            "ORDER BY date, time LIMIT 1")
    LiveData<Appointment> getNextAppointment();

    // ── COMPTEURS SYNCHRONES POUR LE PROFIL ──
    @Query("SELECT COUNT(*) FROM appointments")
    int countAppointments();
    @Query("DELETE FROM appointments")
    void deleteAll();
    @Query("SELECT COUNT(*) FROM appointments WHERE id IS NOT NULL")
    int countOrdonnances();

    // ── ÉTAPE 2 : LE PETIT SAC DE STOCKAGE ADAPTÉ À TES COLONNES ──
    class MedicalPair {
        public String specialty;   // Reçu depuis ton entité Appointment
        public String doctorName;  // Reçu depuis ton entité Appointment
    }

    // ── ÉTAPE 3 : LA REQUÊTE MAGIQUE UNIQUE PAR SPÉCIALITÉ ──
    @Query("SELECT DISTINCT specialty, doctorName FROM appointments WHERE specialty IS NOT NULL AND specialty != ''")
    List<MedicalPair> getAllDiseasesWithDoctors();
}