package com.mobileproject.se77a.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.dao.AppointmentDao;
import com.mobileproject.se77a.database.dao.DoctorDao;
import com.mobileproject.se77a.database.dao.TimeSlotDao;
import com.mobileproject.se77a.database.entities.Appointment;
import com.mobileproject.se77a.database.entities.Doctor;
import com.mobileproject.se77a.database.entities.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppointmentRepository {

    private final DoctorDao      doctorDao;
    private final TimeSlotDao    timeSlotDao;
    private final AppointmentDao appointmentDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AppointmentRepository(Application app) {
        AppDatabase db  = AppDatabase.getInstance(app);
        doctorDao       = db.doctorDao();
        timeSlotDao     = db.timeSlotDao();
        appointmentDao  = db.appointmentDao();

        executor.execute(this::seedIfEmpty);
    }

    // ── Lecture ────────────────────────────────────────────────────────────

    public LiveData<List<String>> getAllSpecialties() {
        return doctorDao.getAllSpecialties();
    }

    public LiveData<List<Doctor>> getDoctorsBySpecialty(String specialty) {
        return doctorDao.getDoctorsBySpecialty(specialty);
    }

    public LiveData<List<TimeSlot>> getAvailableSlots(int doctorId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        return timeSlotDao.getAvailableSlots(doctorId, today);
    }

    public LiveData<List<Appointment>> getUpcomingAppointments() {
        return appointmentDao.getUpcoming();
    }

    public LiveData<Appointment> getNextAppointment() {
        return appointmentDao.getNextAppointment();
    }

    // Cette méthode alimente directement le swiper horizontal du FragmentTracking
    public LiveData<List<Appointment>> getAllAppointments() {
        return appointmentDao.getAll();
    }

    // ── Écriture ───────────────────────────────────────────────────────────

    public void confirmAppointment(Doctor doctor, TimeSlot slot, Runnable onSuccess) {
        executor.execute(() -> {
            Appointment appt  = new Appointment();
            appt.doctorId     = doctor.id;
            appt.timeSlotId   = slot.id;
            appt.doctorName   = doctor.name;
            appt.specialty    = doctor.specialty;
            appt.address      = doctor.address;
            appt.phone        = doctor.phone;
            appt.date         = slot.date;
            appt.time         = slot.time;
            appt.status       = "UPCOMING";
            appt.createdAt    = System.currentTimeMillis();

            appointmentDao.insert(appt);
            timeSlotDao.markUnavailable(slot.id);

            new Handler(Looper.getMainLooper()).post(onSuccess);
        });
    }

    public void cancelAppointment(int appointmentId) {
        executor.execute(() ->
                appointmentDao.updateStatus(appointmentId, "CANCELLED")
        );
    }

    // ── Simulation de données (Seed) ───────────────────────────────────────

    private void seedIfEmpty() {
        if (doctorDao.getCount() > 0) return;

        List<Doctor> doctors = Arrays.asList(
                new Doctor("Dr. Ahmed Benali",   "Cardiologue",   "Cabinet Saint-Roch, Casablanca",  "0522001100"),
                new Doctor("Dr. Fatima Zahra",   "Cardiologue",   "Polyclinique Atlas, Rabat",        "0537002200"),
                new Doctor("Dr. Youssef Alaoui", "Généraliste",   "Centre Médical Hay Riad, Rabat",  "0537003300"),
                new Doctor("Dr. Nadia Tazi",     "Généraliste",   "Cabinet Agdal, Rabat",             "0537004400"),
                new Doctor("Dr. Karim Idrissi",  "Dermatologue",  "Clinique Al Shifa, Casablanca",   "0522005500"),
                new Doctor("Dr. Sara Bennani",   "Pédiatre",      "Cabinet Les Orangers, Rabat",      "0537006600"),
                new Doctor("Dr. Omar Cherkaoui", "Ophtalmologue", "Centre Vision Maroc, Casablanca", "0522007700"),
                new Doctor("Dr. Amina Qatib",    "Neurologue",    "CHU Ibn Sina, Rabat",             "0537008800")
        );

        doctorDao.insertAll(doctors);
        generateSlots();
    }

    private void generateSlots() {
        List<TimeSlot> slots  = new ArrayList<>();
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String[] morningSlots   = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30"};
        String[] afternoonSlots = {"14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};

        for (int doctorId = 1; doctorId <= 8; doctorId++) {
            Calendar day = Calendar.getInstance();

            for (int d = 0; d < 14; d++) {
                day.add(Calendar.DAY_OF_MONTH, 1);

                if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) continue;

                String dateStr = sdf.format(day.getTime());
                String[] times = (doctorId % 2 == 0) ? morningSlots : afternoonSlots;

                for (String t : times) {
                    slots.add(new TimeSlot(doctorId, dateStr, t));
                }
            }
        }
        timeSlotDao.insertAll(slots);
    }
}