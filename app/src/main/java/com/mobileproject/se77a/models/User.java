package com.mobileproject.se77a.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users") // <-- Dis à Room que c'est une table SQL
public class User {

    @PrimaryKey(autoGenerate = true) // <-- Crée un ID automatique (1, 2, 3...)
    private int id;

    private String nom;
    private String email;
    private String password;

    public User(String nom, String email, String password) {
        this.nom = nom;
        this.email = email;
        this.password = password;
    }

    // Android Studio a besoin de ces Getters/Setters pour lire l'objet
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}