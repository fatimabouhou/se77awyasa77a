package com.mobileproject.se77a.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.entities.Appointment;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.repository.AppointmentRepository;
import com.mobileproject.se77a.repository.MedicationRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MedicationRepository  medicationRepo;
    private final AppointmentRepository appointmentRepo;
    private final AppDatabase           db;

    // Médicaments
    public final LiveData<List<Medication>> activeMedications;
    private final LiveData<String>          medProgressText;

    // RDV & ordonnances
    private final LiveData<Integer>       _rdvThisMonth;
    private final LiveData<Appointment>   nextAppointment;
    private final MediatorLiveData<Integer> _ordoCount = new MediatorLiveData<>(); // ← corrigé

    public HomeViewModel(@NonNull Application application) {
        super(application);
        medicationRepo  = new MedicationRepository(application);
        appointmentRepo = new AppointmentRepository(application);
        db              = AppDatabase.getInstance(application);

        activeMedications = medicationRepo.getActiveMedications();
        _rdvThisMonth     = appointmentRepo.getCountThisMonth();
        nextAppointment   = appointmentRepo.getNextAppointment();

        medProgressText = Transformations.map(activeMedications, meds -> {
            if (meds == null || meds.isEmpty()) return "0/0";
            int totalDoses = 0, takenDoses = 0;
            for (Medication med : meds) {
                if (med.isActive) {
                    totalDoses += parseTimes(med.reminderTime).length;
                    takenDoses += parseTimes(med.takenTimes).length;
                }
            }
            return takenDoses + "/" + totalDoses;
        });

        loadOrdoCount();
    }

    public LiveData<String>    getMedProgressText()  { return medProgressText; }
    public LiveData<Integer>   getRdvThisMonth()     { return _rdvThisMonth; }
    public LiveData<Integer>   getOrdoCount()        { return _ordoCount; }
    public LiveData<Appointment> getNextAppointment(){ return nextAppointment; }

    private void loadOrdoCount() {
        _ordoCount.setValue(0);
        _ordoCount.addSource(
                db.prescriptionDao().getCount(),           // ← branché sur la vraie table
                value -> _ordoCount.setValue(value != null ? value : 0)
        );
    }

    private String[] parseTimes(String raw) {
        if (raw == null || raw.trim().isEmpty()) return new String[0];
        return raw.split("\\s*,\\s*");
    }
}