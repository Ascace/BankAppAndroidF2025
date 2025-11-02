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
    protected void onCreate(Bundle savedInstanceState)
    {
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

        if (transaction == null)
        {
            Toast.makeText(this, "Error: No transaction data found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvSender.setText("Sender: " + safeString(transaction.getSenderEmail()));
        tvRecipient.setText("Recipient: " + safeString(transaction.getRecipientEmail()));
        tvAmount.setText("Amount: $" + transaction.getAmount());
        tvMessage.setText("Message: " + safeString(transaction.getMessage()));
        tvStatus.setText("Status: " + safeString(transaction.getStatus()));

        ivBack.setOnClickListener(v -> finish());

        handleTransactionState();

        btnConfirm.setOnClickListener(v -> confirmTransaction());
    }

    private void handleTransactionState()
    {
        long now = System.currentTimeMillis();

        if (transaction.getTimestamp() > now)
        {
            tvStatus.setText("Status: Scheduled (Not yet processed)");
            btnConfirm.setText("Pending");
            btnConfirm.setEnabled(false);
            Toast.makeText(this, "Transaction not yet processed.", Toast.LENGTH_SHORT).show();
        }

        else if ("Completed".equalsIgnoreCase(transaction.getStatus()) && !transaction.isConfirmedByRecipient())
        {
            btnConfirm.setEnabled(true);
            btnConfirm.setText("Confirm Receipt");
        }

        else if (transaction.isConfirmedByRecipient())
        {
            tvStatus.setText("Status: Confirmed");
            btnConfirm.setText("Already Confirmed");
            btnConfirm.setEnabled(false);
        }
    }

    private void confirmTransaction()
    {
        String currentUserEmail = authService.getCurrentUserEmail();
        if (currentUserEmail == null)
        {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!currentUserEmail.equalsIgnoreCase(transaction.getRecipientEmail()))
        {
            Toast.makeText(this, "Only the receiver can confirm this transaction", Toast.LENGTH_SHORT).show();
            return;
        }

        dbService.getUser(authService.getCurrentUserId(), new DatabaseService.UserCallback() {
            @Override
            public void onUserLoaded(User receiver)
            {
                dbService.findUserByEmail(transaction.getSenderEmail(), new DatabaseService.UserCallback() {
                    @Override
                    public void onUserLoaded(User sender)
                    {
                        dbService.completeTransactionAndUpdateBalances(transaction, receiver, sender,
                                new DatabaseService.DatabaseCallback() {
                                    @Override
                                    public void onSuccess(String message)
                                    {
                                        Toast.makeText(TransactionDetailActivity.this, "Transaction confirmed!", Toast.LENGTH_SHORT).show();
                                        tvStatus.setText("Status: Confirmed");
                                        btnConfirm.setEnabled(false);
                                        btnConfirm.setText("Confirmed");
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(TransactionDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onUserNotFound()
                    {
                        Toast.makeText(TransactionDetailActivity.this, "Sender not found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error)
                    {
                        Toast.makeText(TransactionDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onUserNotFound()
            {
                Toast.makeText(TransactionDetailActivity.this, "Receiver not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error)
            {
                Toast.makeText(TransactionDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safeString(String input)
    {
        return input != null ? input : "N/A";
    }
}