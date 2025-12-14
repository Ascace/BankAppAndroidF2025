package com.example.bankly;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankly.Models.Transaction;
import com.example.bankly.responses.TransactionHistoryResponse;
import com.example.bankly.responses.TransactionResponse;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitle, tvSchedule, tvCompletedLabel;
    private RecyclerView recyclerSchedule, recyclerCompleted;

    private TransactionAdapter scheduleAdapter;
    private TransactionAdapter completedAdapter;

    private List<Transaction> allTransactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvSchedule = findViewById(R.id.tvSchedule);
        tvCompletedLabel = findViewById(R.id.tvCompletedLabel);
        recyclerSchedule = findViewById(R.id.recyclerSchedule);
        recyclerCompleted = findViewById(R.id.recyclerCompleted);

        recyclerSchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(this));

        scheduleAdapter = new TransactionAdapter(this, new ArrayList<>());
        completedAdapter = new TransactionAdapter(this, new ArrayList<>());

        recyclerSchedule.setAdapter(scheduleAdapter);
        recyclerCompleted.setAdapter(completedAdapter);

        ivBack.setOnClickListener(v -> finish());

        loadTransactions();
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

                            separateTransactions();

                        } else {
                            Toast.makeText(TransactionListActivity.this,
                                    "Failed to load transactions",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                        Toast.makeText(TransactionListActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void separateTransactions() {

        List<Transaction> scheduled = new ArrayList<>();
        List<Transaction> completed = new ArrayList<>();

        for (Transaction t : allTransactions) {

            String type = t.getType();

            if (type == null) {
                completed.add(t);
                continue;
            }

            if (type.equalsIgnoreCase("DEPOSIT") ||
                    type.equalsIgnoreCase("WITHDRAWAL")) {

                completed.add(t);
            }
            else {
                scheduled.add(t);
            }
        }

        scheduleAdapter.updateTransactions(scheduled);
        completedAdapter.updateTransactions(completed);

        tvSchedule.setText("Scheduled Transactions: " + scheduled.size());
        tvCompletedLabel.setText("Completed Transactions (" + completed.size() + ")");
    }
}