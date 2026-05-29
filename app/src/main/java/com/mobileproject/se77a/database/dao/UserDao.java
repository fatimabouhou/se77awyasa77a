package com.mobileproject.se77a.database.dao; // <-- Le package a changé ici !

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.mobileproject.se77a.models.User;

@Dao
public interface UserDao {

    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);
}