package com.example.bankly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;

public class DepositActivity extends AppCompatActivity {

    private EditText etAmount;
    private Button btnDeposit;

    private AuthService authService;
    private DatabaseService databaseService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        etAmount = findViewById(R.id.et_amount);
        btnDeposit = findViewById(R.id.btn_deposit);

        authService = new AuthService();
        databaseService = new DatabaseService();

        loadCurrentUser();

        btnDeposit.setOnClickListener(v -> depositAmount());
    }

    private void loadCurrentUser() {
        String uid = authService.getCurrentUserId();
        if (uid != null) {
            databaseService.getUser(uid, new DatabaseService.UserCallback() {
                @Override
                public void onUserLoaded(User user) {
                    currentUser = user;
                }

                @Override
                public void onUserNotFound() {
                    Toast.makeText(DepositActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(DepositActivity.this, "Error loading user: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void depositAmount() {
        if (currentUser == null) {
            Toast.makeText(this, "User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter amount to deposit", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        double newBalance = currentUser.getBalance() + amount;

        databaseService.updateUserBalance(currentUser.getUid(), newBalance, new DatabaseService.DatabaseCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(DepositActivity.this, "Deposit successful", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DepositActivity.this, "Error updating balance: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
