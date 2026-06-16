package com.mobileproject.se77a.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.mobileproject.se77a.database.entities.Analysis;
import java.util.List;

@Dao
public interface AnalysisDao {

    @Insert
    void insert(Analysis analysis);

    @Update
    void update(Analysis analysis);

    @Delete
    void delete(Analysis analysis);

    @Query("SELECT * FROM analyses ORDER BY date DESC")
    LiveData<List<Analysis>> getAllAnalyses();
}