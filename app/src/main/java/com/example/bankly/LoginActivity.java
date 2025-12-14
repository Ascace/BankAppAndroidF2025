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

import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;
import com.example.bankly.requests.LoginRequest;
import com.example.bankly.responses.AuthResponse;
import com.example.bankly.responses.ApiErrorResponse;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnSignUp;
    private EditText etEmail, etPassword;
    private AuthService authService;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        authService = new AuthService();
        databaseService = new DatabaseService();

        if (TokenManager.hasToken(this)) {
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

            // Login with API
            loginUser(email, password);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.card_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginUser(String email, String password) {
        // Call API to login
        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getInstance()
                .create(BankApiService.class)
                .login(request)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getToken();

                            // Save JWT token
                            TokenManager.saveToken(LoginActivity.this, token);

                            Toast.makeText(LoginActivity.this,
                                    "Login successful!",
                                    Toast.LENGTH_SHORT).show();

                            // Also sign in to Firebase (for scheduled transactions database)
                            signInToFirebase(email, password);

                        } else {
                            // Handle error
                            try {
                                Gson gson = new Gson();
                                ApiErrorResponse errorResponse = gson.fromJson(
                                        response.errorBody().charStream(),
                                        ApiErrorResponse.class
                                );

                                String errorMsg = errorResponse.getError();

                                // Check if account not activated
                                if (errorMsg != null && errorMsg.toLowerCase().contains("not activated")) {
                                    Toast.makeText(LoginActivity.this,
                                            "Please activate your account first!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            errorMsg != null ? errorMsg : "Invalid email or password",
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this,
                                        "Login failed! Please check your credentials.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInToFirebase(String email, String password) {
        // Sign in to Firebase for database access (scheduled transactions)
        authService.signInUser(email, password, this, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Firebase login successful, go to home
                goToHome();
            }

            @Override
            public void onFailure(String error) {
                // If Firebase user doesn't exist, create it
                // i am assuming it happen if user signed up via API but Firebase user was never created
                authService.signUpUser(email, password, LoginActivity.this, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser firebaseUser) {
                        // Create user in Firebase database
                        User user = new User(firebaseUser.getUid(), email, 0.00);

                        databaseService.saveUser(user, new DatabaseService.DatabaseCallback() {
                            @Override
                            public void onSuccess(String message) {
                                goToHome();
                            }

                            @Override
                            public void onFailure(String error) {
                                goToHome(); // Go to home anyway
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        // Even if Firebase fails, user can still use the app with API
                        Toast.makeText(LoginActivity.this,
                                "Logged in! (Firebase sync failed, scheduled transactions may not work)",
                                Toast.LENGTH_LONG).show();
                        goToHome();
                    }
                });
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}