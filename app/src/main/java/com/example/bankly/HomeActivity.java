package com.example.bankly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankly.Models.Transaction;
import com.example.bankly.Models.User;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUserEmail, tvBalance;
    private ImageView ivLogout;
    private Button btnScheduled, btnCompleted;
    private Button btnWithdraw, btnDeposit;
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private FloatingActionButton fabAddTransaction;

    private AuthService authService;
    private DatabaseService databaseService;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private String currentFilter = "scheduled"; // "scheduled" or "completed"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Services
        authService = new AuthService();
        databaseService = new DatabaseService();

        // Views
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvBalance = findViewById(R.id.tv_balance);
        ivLogout = findViewById(R.id.iv_logout);
        btnScheduled = findViewById(R.id.btn_scheduled);
        btnCompleted = findViewById(R.id.btn_completed);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnDeposit = findViewById(R.id.btn_deposit);
        recyclerView = findViewById(R.id.recycler_view);
        emptyState = findViewById(R.id.empty_state);
        fabAddTransaction = findViewById(R.id.fab_add_transaction);

        // Display user email and balance
        String email = authService.getCurrentUserEmail();
        if (email != null) {
            tvUserEmail.setText(email);
            loadUserBalance();
        }

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Filters
        btnScheduled.setOnClickListener(v -> {
            currentFilter = "scheduled";
            updateButtonStates();
            filterTransactions();
        });

        btnCompleted.setOnClickListener(v -> {
            currentFilter = "completed";
            updateButtonStates();
            filterTransactions();
        });

        // Withdraw / Deposit buttons
        btnWithdraw.setOnClickListener(v -> {
            // Open WithdrawActivity (implement later)
            startActivity(new Intent(HomeActivity.this, WithdrawActivity.class));
        });

        btnDeposit.setOnClickListener(v -> {
            // Open DepositActivity (implement later)
            startActivity(new Intent(HomeActivity.this, DepositActivity.class));
        });

        // Logout
        ivLogout.setOnClickListener(v -> {
            authService.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Add transaction
        fabAddTransaction.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CreateTransactionActivity.class));
        });

        // Load transactions
        loadTransactions();
    }

    private void updateButtonStates() {
        if (currentFilter.equals("scheduled")) {
            btnScheduled.setBackgroundTintList(getColorStateList(android.R.color.white));
            btnScheduled.setTextColor(getColor(android.R.color.holo_blue_dark));
            btnCompleted.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));
            btnCompleted.setTextColor(getColor(android.R.color.white));
        } else {
            btnScheduled.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_light));
            btnScheduled.setTextColor(getColor(android.R.color.white));
            btnCompleted.setBackgroundTintList(getColorStateList(android.R.color.white));
            btnCompleted.setTextColor(getColor(android.R.color.holo_blue_dark));
        }
    }

    private void loadUserBalance() {
        String uid = authService.getCurrentUserId();
        if (uid != null) {
            databaseService.getUser(uid, new DatabaseService.UserCallback() {
                @Override
                public void onUserLoaded(User user) {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                    tvBalance.setText(formatter.format(user.getBalance()));
                }

                @Override
                public void onUserNotFound() {
                    tvBalance.setText("$0.00");
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(HomeActivity.this, "Error loading balance", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadTransactions() {
        String email = authService.getCurrentUserEmail();
        if (email != null) {
            databaseService.getUserTransactions(email, new DatabaseService.TransactionListCallback() {
                @Override
                public void onTransactionsLoaded(List<Transaction> transactions) {
                    allTransactions = transactions;
                    filterTransactions();
                    Toast.makeText(HomeActivity.this, "Transactions loaded: " + transactions.size(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(HomeActivity.this, "Error loading transactions", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void filterTransactions() {
        List<Transaction> filtered = new ArrayList<>();

        for (Transaction transaction : allTransactions) {
            if (currentFilter.equals("scheduled")) {
                if (transaction.getStatus().equals("Scheduled") || transaction.getStatus().equals("Processing")) {
                    filtered.add(transaction);
                }
            } else {
                if (transaction.getStatus().equals("Completed") || transaction.getStatus().equals("Failed")) {
                    filtered.add(transaction);
                }
            }
        }

        adapter.updateTransactions(filtered);

        if (filtered.isEmpty()) {
            emptyState.setVisibility(LinearLayout.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            emptyState.setVisibility(LinearLayout.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserBalance();
        loadTransactions();
    }
}
