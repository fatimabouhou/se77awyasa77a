package com.mobileproject.se77a.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users") // Dis à Room que c'est une table SQL
public class User {

    @PrimaryKey(autoGenerate = true) // Crée un ID automatique (1, 2, 3...)
    private int id;

    private String nom;
    private String email;
    private String password;

    // ── NOUVELLES COLONNES POUR LE PROFIL MÉDICAL ──
    private int age;
    private String groupeSanguin;
    private int taille;
    private int poids;

    // Ton constructeur principal (qui reste identique)
    public User(String nom, String email, String password) {
        this.nom = nom;
        this.email = email;
        this.password = password;
    }

    // ── GETTERS & SETTERS STANDARDS ──
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // ── NOUVEAUX GETTERS & SETTERS POUR LES BADGES DU PROFIL ──
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public int getTaille() { return taille; }
    public void setTaille(int taille) { this.taille = taille; }

    public int getPoids() { return poids; }
    public void setPoids(int poids) { this.poids = poids; }
}