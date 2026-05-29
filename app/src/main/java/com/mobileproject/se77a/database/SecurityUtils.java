package com.mobileproject.se77a.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    public static String hashPassword(String password) {
        try {
            // On utilise l'algorithme SHA-256 de Java
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            // Convertir le résultat en texte (Hexadécimal)
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // En cas d'erreur rare, on renvoie le mot de passe de base
        }
    }
}