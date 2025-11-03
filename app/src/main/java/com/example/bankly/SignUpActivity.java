package com.example.bankly;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnCreateAccount;
    private TextView tvLogin;
    private ImageView ivBack;

    private AuthService authService;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        authService = new AuthService();
        databaseService = new DatabaseService();


        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        tvLogin = findViewById(R.id.tv_login);
        ivBack = findViewById(R.id.iv_back);


        ivBack.setOnClickListener(v -> finish());

        // Create account button
        btnCreateAccount.setOnClickListener(v -> createAccount());


        tvLogin.setOnClickListener(v -> finish());
    }

    private void createAccount() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validates
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

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Create account
        authService.signUpUser(email, password, this, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                // Create user in database with initial balance of 0$
                User user = new User(firebaseUser.getUid(), email, 0.00);

                databaseService.saveUser(user, new DatabaseService.DatabaseCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(SignUpActivity.this,
                                "Welcome to Bankly!, Enjoy the experience ", Toast.LENGTH_LONG).show();
                        goToHome();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(SignUpActivity.this,
                                "Account created but error saving data: " + error,
                                Toast.LENGTH_LONG).show();
                        goToHome();
                    }
                });
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}