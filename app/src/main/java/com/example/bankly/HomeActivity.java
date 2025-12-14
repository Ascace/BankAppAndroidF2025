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
import com.example.bankly.responses.BalanceResponse;
import com.example.bankly.responses.TransactionHistoryResponse;
import com.example.bankly.responses.TransactionResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private String currentFilter = "scheduled";

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

        String email = authService.getCurrentUserEmail();
        if (email != null) {
            tvUserEmail.setText(email);
            loadUserBalance();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

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

        btnWithdraw.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, WithdrawActivity.class)));

        btnDeposit.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, DepositActivity.class)));

        ivLogout.setOnClickListener(v -> {
            authService.signOut();
            TokenManager.clearToken(HomeActivity.this);

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Create transaction button
        fabAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CreateTransactionActivity.class)));

        // Load API transactions
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

        RetrofitClient.getInstanceWithAuth(this)
                .create(BankApiService.class)
                .getBalance()
                .enqueue(new Callback<BalanceResponse>() {
                    @Override
                    public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            double balance = response.body().getBalance();

                            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                            tvBalance.setText(formatter.format(balance));

                        } else {
                            tvBalance.setText("$0.00");
                        }
                    }

                    @Override
                    public void onFailure(Call<BalanceResponse> call, Throwable t) {
                        tvBalance.setText("$0.00");
                    }
                });
    }

    private void loadTransactions() {

        RetrofitClient.getInstanceWithAuth(this)
                .create(BankApiService.class)
                .getTransactions(30)
                .enqueue(new Callback<TransactionHistoryResponse>() {
                    @Override
                    public void onResponse(Call<TransactionHistoryResponse> call,
                                           Response<TransactionHistoryResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            allTransactions = response.body().getStatement();
                            if (allTransactions == null)
                                allTransactions = new ArrayList<>();

                            filterTransactions();

                            Toast.makeText(HomeActivity.this,
                                    "Transactions loaded: " + allTransactions.size(),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(HomeActivity.this,
                                    "Failed to load transactions",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                        Toast.makeText(HomeActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterTransactions() {

        List<Transaction> filtered = new ArrayList<>();

        if (currentFilter.equals("scheduled")) {
            filtered = new ArrayList<>();

        } else {
            filtered = new ArrayList<>(allTransactions);
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
