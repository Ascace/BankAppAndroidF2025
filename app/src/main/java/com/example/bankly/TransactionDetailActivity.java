package com.example.bankly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.Models.Transaction;
import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;

public class TransactionDetailActivity extends AppCompatActivity {

    private TextView tvSender, tvRecipient, tvAmount, tvMessage, tvStatus;
    private Button btnConfirm;
    private ImageView ivBack;

    private Transaction transaction;
    private AuthService authService;
    private DatabaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);


        tvSender = findViewById(R.id.tvSender);
        tvRecipient = findViewById(R.id.tvRecipient);
        tvAmount = findViewById(R.id.tvAmount);
        tvMessage = findViewById(R.id.tvMessage);
        tvStatus = findViewById(R.id.tvStatus);
        btnConfirm = findViewById(R.id.btnConfirm);
        ivBack = findViewById(R.id.ivBack);

        authService = new AuthService();
        dbService = new DatabaseService();


        transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        if (transaction == null) {
            Toast.makeText(this, "Error: No transaction data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set UI safely
        tvSender.setText("Sender: " + safeString(transaction.getSenderEmail()));
        tvRecipient.setText("Recipient: " + safeString(transaction.getRecipientEmail()));
        tvAmount.setText("Amount: $" + transaction.getAmount());
        tvMessage.setText("Message: " + safeString(transaction.getMessage()));
        tvStatus.setText("Status: " + safeString(transaction.getStatus()));

        ivBack.setOnClickListener(v -> finish());

        setupConfirmButton();
    }

    private void setupConfirmButton() {
        // Disable if already confirmed or completed
        if (transaction.isConfirmedByRecipient() || "Completed".equalsIgnoreCase(transaction.getStatus())) {
            btnConfirm.setText("Already Confirmed");
            btnConfirm.setEnabled(false);
            return;
        }

        btnConfirm.setOnClickListener(v -> confirmTransaction());
    }

    private void confirmTransaction() {
        String currentUserEmail = authService.getCurrentUserEmail();
        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Only the recipient can confirm
        if (!currentUserEmail.equalsIgnoreCase(transaction.getRecipientEmail())) {
            Toast.makeText(this, "Only the recipient can confirm this transaction", Toast.LENGTH_SHORT).show();
            return;
        }

        dbService.getUser(authService.getCurrentUserId(), new DatabaseService.UserCallback() {
            @Override
            public void onUserLoaded(User receiver) {
                if (receiver == null) {
                    Toast.makeText(TransactionDetailActivity.this, "Receiver not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbService.findUserByEmail(transaction.getSenderEmail(), new DatabaseService.UserCallback() {
                    @Override
                    public void onUserLoaded(User sender) {
                        if (sender == null) {
                            Toast.makeText(TransactionDetailActivity.this, "Sender not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dbService.completeTransactionAndUpdateBalances(transaction, receiver, sender, new DatabaseService.DatabaseCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(TransactionDetailActivity.this, "Transaction confirmed!", Toast.LENGTH_SHORT).show();
                                transaction.setStatus("Completed");
                                transaction.setConfirmedByRecipient(true);
                                tvStatus.setText("Status: Completed");
                                btnConfirm.setText("Confirmed");
                                btnConfirm.setEnabled(false);
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(TransactionDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onUserNotFound() {
                        Toast.makeText(TransactionDetailActivity.this, "Sender not found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(TransactionDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUserNotFound() {
                Toast.makeText(TransactionDetailActivity.this, "Receiver not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TransactionDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safeString(String input) {
        return input != null ? input : "N/A";
    }
}
