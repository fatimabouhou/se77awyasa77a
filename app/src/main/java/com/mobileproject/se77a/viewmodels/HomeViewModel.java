package com.mobileproject.se77a.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.entities.Medication;
import com.mobileproject.se77a.models.Appointment; // ton entité RDV
import com.mobileproject.se77a.repository.MedicationRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MedicationRepository medicationRepo;
    private final AppDatabase db;

    // Médicaments
    public final LiveData<Integer> activeMedCount;
    public final LiveData<Integer> takenTodayCount;
    public final LiveData<List<Medication>> activeMedications;

    // Ordonnances & RDV — à brancher sur tes DAOs quand prêts
    private final MutableLiveData<Integer> _rdvThisMonth = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> _ordoCount    = new MutableLiveData<>(0);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        medicationRepo   = new MedicationRepository(application);
        db               = AppDatabase.getInstance(application);

        activeMedCount   = medicationRepo.getActiveMedicationCount();
        takenTodayCount  = medicationRepo.getTakenTodayCount();
        activeMedications = medicationRepo.getActiveMedications();

        // TODO : remplacer par un vrai DAO quand AppointmentRepository sera créé
        loadRdvThisMonth();
        loadOrdoCount();
    }

    public LiveData<Integer> getRdvThisMonth() { return _rdvThisMonth; }
    public LiveData<Integer> getOrdoCount()    { return _ordoCount; }

    private void loadRdvThisMonth() {
        // Branche ici sur AppointmentDao.getCountThisMonth() quand disponible
        _rdvThisMonth.setValue(0);
    }

    private void loadOrdoCount() {
        // Branche ici sur PrescriptionDao.getCount() quand disponible
        _ordoCount.setValue(0);
    }
}