package com.mobileproject.se77a.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mobileproject.se77a.R;
import com.mobileproject.se77a.models.User;
import com.mobileproject.se77a.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNom, etEmail, etPassword, etConfirmPassword;
    // Nouveaux champs ajoutés pour correspondre au Layout XML
    private EditText etAge, etGroupeSanguin, etTaille, etPoids;
    private Button btnRegister;
    private TextView tvGoLogin;

    // Le Repository Room relié à ton écran
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisation de notre gestionnaire de données
        userRepository = new UserRepository(this);

        // Liaison des vues initiales
        etNom = findViewById(R.id.etNom);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        // Liaison des nouvelles vues du profil santé
        etAge = findViewById(R.id.etAge);
        etGroupeSanguin = findViewById(R.id.etGroupeSanguin);
        etTaille = findViewById(R.id.etTaille);
        etPoids = findViewById(R.id.etPoids);

        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> {
            String nom = etNom.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            // Récupération des chaînes des nouveaux champs
            String age = etAge.getText().toString().trim();
            String groupeSanguin = etGroupeSanguin.getText().toString().trim();
            String taille = etTaille.getText().toString().trim();
            String poids = etPoids.getText().toString().trim();

            // Vérification des champs obligatoires standards
            if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs obligatoires", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Création de l'objet utilisateur initial
            User nouvelUtilisateur = new User(nom, email, password);

            // Injection des paramètres de santé optionnels dans l'objet User avant sauvegarde Room
            if (!age.isEmpty()) {
                try {
                    nouvelUtilisateur.setAge(Integer.parseInt(age));
                } catch (NumberFormatException e) {
                    // Sécurité en cas de mauvaise saisie numérique
                }
            }
            if (!groupeSanguin.isEmpty()) {
                nouvelUtilisateur.setGroupeSanguin(groupeSanguin);
            }
            if (!taille.isEmpty()) {
                try {
                    nouvelUtilisateur.setTaille(Integer.parseInt(taille));
                } catch (NumberFormatException e) { }
            }
            if (!poids.isEmpty()) {
                try {
                    nouvelUtilisateur.setPoids(Integer.parseInt(poids));
                } catch (NumberFormatException e) { }
            }

            // Envoi au repository pour vérification et sauvegarde Room
            boolean isSuccess = userRepository.registerUser(nouvelUtilisateur);

            if (isSuccess) {
                // Sauvegarde de l'intégralité des données utilisateur pour rafraîchir FragmentProfile
                getSharedPreferences("user_prefs", MODE_PRIVATE)
                        .edit()
                        .putString("current_user_name", nom)
                        .putString("email", email)
                        .putString("age", age.isEmpty() ? "—" : age)
                        .putString("groupe_sanguin", groupeSanguin.isEmpty() ? "—" : groupeSanguin)
                        .putString("taille", taille.isEmpty() ? "—" : taille)
                        .putString("poids", poids.isEmpty() ? "—" : poids)
                        .apply();

                Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Erreur : Cet email est déjà utilisé !", Toast.LENGTH_LONG).show();
            }
        });

        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}