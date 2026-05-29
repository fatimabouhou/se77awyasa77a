package com.mobileproject.se77a.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.mobileproject.se77a.database.dao.UserDao; // L'import propre grâce à ton sous-dossier !
import com.mobileproject.se77a.models.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract UserDao userDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "healthtracker_room.db")
                    .allowMainThreadQueries() // Permet de tester ton application de façon simple
                    .build();
        }
        return instance;
    }
}

// par la suite je vais supprimmee had allowmainthreadqueries bach manfreezeech l'app dyalii n9dr nkhdm b executor