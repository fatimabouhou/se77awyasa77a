package com.mobileproject.se77a.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.dao.PrescriptionDao;
import com.mobileproject.se77a.database.entities.Prescription;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrescriptionRepository {

    private final PrescriptionDao     prescriptionDao;
    private final ExecutorService     executor;

    public PrescriptionRepository(Application application) {
        AppDatabase db  = AppDatabase.getInstance(application);
        prescriptionDao = db.prescriptionDao();
        executor        = Executors.newSingleThreadExecutor();
    }

    public void insert(Prescription prescription) {
        executor.execute(() -> prescriptionDao.insert(prescription));
    }

    public void delete(Prescription prescription) {
        executor.execute(() -> prescriptionDao.delete(prescription));
    }

    public LiveData<List<Prescription>> getAllPrescriptions() {
        return prescriptionDao.getAllPrescriptions();
    }
}