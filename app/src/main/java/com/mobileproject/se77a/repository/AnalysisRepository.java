package com.mobileproject.se77a.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.dao.AnalysisDao;
import com.mobileproject.se77a.database.entities.Analysis;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalysisRepository {

    private final AnalysisDao     analysisDao;
    private final ExecutorService executor;

    public AnalysisRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        analysisDao    = db.analysisDao();
        executor       = Executors.newSingleThreadExecutor();
    }

    public void insert(Analysis analysis) {
        executor.execute(() -> analysisDao.insert(analysis));
    }

    public void delete(Analysis analysis) {
        executor.execute(() -> analysisDao.delete(analysis));
    }

    public LiveData<List<Analysis>> getAllAnalyses() {
        return analysisDao.getAllAnalyses();
    }
}