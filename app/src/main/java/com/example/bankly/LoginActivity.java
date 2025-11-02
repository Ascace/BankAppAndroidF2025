package com.example.bankly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bankly.HomeActivity;
import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnSignUp;
    private EditText etEmail, etPassword;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        authService = new AuthService();


        if (authService.getCurrentUser() != null) {
            goToHome();
            return;
        }

        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_signup);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }

            // Login with Firebase
            loginUser(email, password);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.card_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginUser(String email, String password) {
        authService.signInUser(email, password, this, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Login successful, go to home
                goToHome();
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); 
    }
}