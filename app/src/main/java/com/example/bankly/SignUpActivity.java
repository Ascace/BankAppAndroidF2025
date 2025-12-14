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
import com.example.bankly.requests.CreateAccountRequest;
import com.example.bankly.responses.AuthResponse;
import com.example.bankly.responses.ApiErrorResponse;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnCreateAccount;
    private TextView tvLogin;
    private ImageView ivBack;

    private AuthService authService;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize services
        authService = new AuthService();
        databaseService = new DatabaseService();

        // Find views - NEW FIELDS
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        tvLogin = findViewById(R.id.tv_login);
        ivBack = findViewById(R.id.iv_back);

        // Back button
        ivBack.setOnClickListener(v -> finish());

        // Create account button
        btnCreateAccount.setOnClickListener(v -> createAccount());

        // Login link
        tvLogin.setOnClickListener(v -> finish());
    }

    private void createAccount() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
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

        // Create account via API
        CreateAccountRequest request = new CreateAccountRequest(firstName, lastName, email, phone, password);

        RetrofitClient.getInstance()
                .create(BankApiService.class)
                .createAccount(request)
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(SignUpActivity.this,
                                    "Account created! Please activate your account.",
                                    Toast.LENGTH_LONG).show();

                            // Go to ActivationActivity
                            Intent intent = new Intent(SignUpActivity.this, ActivationActivity.class);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("PASSWORD", password);
                            intent.putExtra("FIRST_NAME", firstName);
                            intent.putExtra("LAST_NAME", lastName);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle error
                            try {
                                Gson gson = new Gson();
                                ApiErrorResponse errorResponse = gson.fromJson(
                                        response.errorBody().charStream(),
                                        ApiErrorResponse.class
                                );
                                Toast.makeText(SignUpActivity.this,
                                        errorResponse.getError(),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(SignUpActivity.this,
                                        "Error creating account: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}