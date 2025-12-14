package com.example.bankly;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.Models.Transaction;
import com.example.bankly.Models.Account;

public class TransactionDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvSender, tvRecipient, tvAmount, tvMessage, tvStatus, tvExtraMessage;
    private Button btnConfirm;

    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        ivBack = findViewById(R.id.ivBack);
        tvSender = findViewById(R.id.tvSender);
        tvRecipient = findViewById(R.id.tvRecipient);
        tvAmount = findViewById(R.id.tvAmount);
        tvMessage = findViewById(R.id.tvMessage);
        tvStatus = findViewById(R.id.tvStatus);
        tvExtraMessage = findViewById(R.id.tvExtraMessage);
        btnConfirm = findViewById(R.id.btnConfirm);

        transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        if (transaction != null) {
            loadTransactionDetails();
        }

        ivBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            btnConfirm.setEnabled(false);
            btnConfirm.setText("Confirmed");
            tvStatus.setText("Status: Confirmed");
        });
    }

    private void loadTransactionDetails() {

        tvSender.setText("Sender: " +
                (transaction.getFromAccount() != null
                        ? transaction.getFromAccount().getEmail()
                        : "Unknown"));

        tvRecipient.setText("Recipient: " +
                (transaction.getToAccount() != null
                        ? transaction.getToAccount().getEmail()
                        : "Unknown"));

        tvAmount.setText("Amount: $" + transaction.getAmount());
        tvMessage.setText("Message: " + transaction.getMessage());

        String type = transaction.getType();
        tvStatus.setText("Type: " + type);

        btnConfirm.setVisibility(View.GONE);
        tvExtraMessage.setVisibility(View.GONE);
    }

}