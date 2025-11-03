package com.example.bankly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.Models.Transaction;
import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;

import java.util.Calendar;

public class CreateTransactionActivity extends AppCompatActivity {

    private EditText etEmail, etAmount, etMessage;
    private Button btn_send_money;
    private ImageView ivBack;
    private CalendarView calendarView;
    private long selectedDateMillis = System.currentTimeMillis();

    private AuthService authService;
    private DatabaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        etEmail = findViewById(R.id.etEmail);
        etAmount = findViewById(R.id.et_amount);
        etMessage = findViewById(R.id.et_message);
        btn_send_money = findViewById(R.id.btn_send_money);
        ivBack = findViewById(R.id.iv_back);
        calendarView = findViewById(R.id.calendarView);

        authService = new AuthService();
        dbService = new DatabaseService();

        ivBack.setOnClickListener(v -> finish());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDateMillis = c.getTimeInMillis();
        });

        btn_send_money.setOnClickListener(v -> createTransaction());
    }

    private void createTransaction() {
        String recipientEmail = etEmail.getText().toString().trim();
        String amountText = etAmount.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (recipientEmail.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Email and amount are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderId = authService.getCurrentUserId();
        String senderEmail = authService.getCurrentUserEmail();

        if (senderId == null || senderEmail == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }


        dbService.findUserByEmail(recipientEmail, new DatabaseService.UserCallback() {
            @Override
            public void onUserLoaded(User recipient) {
                if (recipient.getEmail().equals(senderEmail)) {
                    Toast.makeText(CreateTransactionActivity.this, "You cannot send money to yourself", Toast.LENGTH_SHORT).show();
                    return;
                }


                dbService.getUser(senderId, new DatabaseService.UserCallback() {
                    @Override
                    public void onUserLoaded(User sender) {
                        if (sender.getBalance() < amount) {
                            Toast.makeText(CreateTransactionActivity.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Transaction newTransaction = new Transaction(
                                senderId,
                                senderEmail,
                                recipientEmail,
                                amount,
                                message.isEmpty() ? "No message" : message,
                                "Scheduled",
                                selectedDateMillis
                        );

                        dbService.createTransaction(newTransaction, new DatabaseService.DatabaseCallback() {
                            @Override
                            public void onSuccess(String msg) {
                                Toast.makeText(CreateTransactionActivity.this, "Transaction scheduled!", Toast.LENGTH_SHORT).show();
                                etEmail.setText("");
                                etAmount.setText("");
                                etMessage.setText("");
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(CreateTransactionActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onUserNotFound() {
                        Toast.makeText(CreateTransactionActivity.this, "Sender not found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(CreateTransactionActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUserNotFound() {
                Toast.makeText(CreateTransactionActivity.this, "Recipient not registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreateTransactionActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
