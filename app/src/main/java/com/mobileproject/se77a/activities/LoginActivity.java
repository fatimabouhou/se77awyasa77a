package com.mobileproject.se77a.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.mobileproject.se77a.repository.UserRepository; // Ajuste l'import selon ton projet
import com.mobileproject.se77a.database.SecurityUtils; // Si tu haches le mot de passe

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvGoRegister;

    // 1. Déclarer le Repository
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        // 2. Initialiser le Repository
        userRepository = new UserRepository(getApplication());

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Vérification avec Room
            // Si tu as utilisé le hachage MD5/SHA à l'inscription, applique-le aussi ici :
            String hashedPassword = SecurityUtils.hashPassword(password);

            // On demande au repository de chercher l'utilisateur
            User user = userRepository.login(email, hashedPassword);

            if (user != null) {
                // Sauvegarder le nom pour l'affichage dans FragmentHome
                SharedPreferences prefs =
                        getSharedPreferences("user_prefs", MODE_PRIVATE);

                prefs.edit()
                        .putString("current_user_name", user.getNom())
                        .apply();

                Toast.makeText(this, "Connexion réussie ! Bienvenue " + user.getNom(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Échec : Email ou mot de passe incorrect
                Toast.makeText(this, "Utilisateur non trouvé. Essayez de vous ré-inscrire (Base de données mise à jour)", Toast.LENGTH_LONG).show();
            }
        });

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
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