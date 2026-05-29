package com.mobileproject.se77a.repository;

import android.content.Context;
import com.mobileproject.se77a.database.AppDatabase;
import com.mobileproject.se77a.database.dao.UserDao;
import com.mobileproject.se77a.database.SecurityUtils;
import com.mobileproject.se77a.models.User;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.userDao = db.userDao();
    }

    // Ton code d'inscription (parfait)
    public boolean registerUser(User user) {
        if (userDao.getUserByEmail(user.getEmail()) != null) {
            return false;
        }

        String passwordHache = SecurityUtils.hashPassword(user.getPassword());
        user.setPassword(passwordHache);

        long result = userDao.insertUser(user);
        return result != -1;
    }

    // =========================================================
    // AJOUTE CETTE MÉTHODE POUR LE LOGIN :
    // =========================================================
    public User login(String email, String password) {
        // On demande directement au DAO de chercher dans la base SQLite
        return userDao.login(email, password);
    }
}