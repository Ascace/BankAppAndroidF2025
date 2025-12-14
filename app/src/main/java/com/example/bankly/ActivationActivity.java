package com.example.bankly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;
import com.example.bankly.requests.ActivateAccountRequest;
import com.example.bankly.requests.LoginRequest;
import com.example.bankly.responses.AuthResponse;
import com.example.bankly.responses.ApiErrorResponse;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationActivity extends AppCompatActivity {

    private EditText etActivationCode;
    private Button btnActivate;
    private TextView tvResendCode, tvEmail;

    private String email, password, firstName, lastName;
    private AuthService authService;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        // Get data
        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");

        // Initialize services
        authService = new AuthService();
        databaseService = new DatabaseService();

        // Find views
        etActivationCode = findViewById(R.id.et_activation_code);
        btnActivate = findViewById(R.id.btn_activate);
        tvResendCode = findViewById(R.id.tv_resend_code);
        tvEmail = findViewById(R.id.tv_email);

        // Display email
        tvEmail.setText("We sent a code to " + email);

        // Activate button
        btnActivate.setOnClickListener(v -> activateAccount());

        // Resend code
        tvResendCode.setOnClickListener(v -> {
            Toast.makeText(this, "Use code: 123456", Toast.LENGTH_LONG).show();
        });
    }

    private void activateAccount() {
        String code = etActivationCode.getText().toString().trim();

        if (code.isEmpty()) {
            etActivationCode.setError("Please enter activation code");
            etActivationCode.requestFocus();
            return;
        }

        // Call API
        ActivateAccountRequest request = new ActivateAccountRequest(email, code);

        RetrofitClient.getInstance()
                .create(BankApiService.class)
                .activateAccount(request)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ActivationActivity.this,
                                    "Account activated successfully!",
                                    Toast.LENGTH_SHORT).show();


                            autoLogin();
                        } else {
                            try {
                                Gson gson = new Gson();
                                ApiErrorResponse errorResponse = gson.fromJson(
                                        response.errorBody().charStream(),
                                        ApiErrorResponse.class
                                );
                                Toast.makeText(ActivationActivity.this,
                                        errorResponse.getError(),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ActivationActivity.this,
                                        "Invalid activation code!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(ActivationActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void autoLogin() {
        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getInstance()
                .create(BankApiService.class)
                .login(request)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getToken();


                            TokenManager.saveToken(ActivationActivity.this, token);

                            // Also create Firebase user for database access
                            createFirebaseUser();
                        } else {
                            Toast.makeText(ActivationActivity.this,
                                    "Activated but login failed. Please login manually.",
                                    Toast.LENGTH_LONG).show();
                            goToLogin();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(ActivationActivity.this,
                                "Login failed: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                        goToLogin();
                    }
                });
    }

    private void createFirebaseUser() {
        // Create Firebase user for database access (scheduled transactions)
        authService.signUpUser(email, password, this, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                // Create user in Firebase database
                User user = new User(firebaseUser.getUid(), email, 0.00);

                databaseService.saveUser(user, new DatabaseService.DatabaseCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ActivationActivity.this,
                                "Welcome to Bankly!",
                                Toast.LENGTH_SHORT).show();
                        goToHome();
                    }

                    @Override
                    public void onFailure(String error) {
                        // Still go to home even if Firebase save fails
                        goToHome();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                // If Firebase user already exists, just login
                authService.signInUser(email, password, ActivationActivity.this, new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        goToHome();
                    }

                    @Override
                    public void onFailure(String error) {
                        goToHome(); // Go to home anyway, API auth is primaryy
                    }
                });
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(ActivationActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(ActivationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}