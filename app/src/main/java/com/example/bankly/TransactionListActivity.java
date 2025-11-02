package com.example.bankly;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankly.R;


import com.example.bankly.Models.Transaction;
import com.example.bankly.Services.AuthService;
import com.example.bankly.Services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity {

    private RecyclerView recyclerSchedule, recyclerCompleted;
    private TransactionAdapter scheduledAdapter, completedAdapter;
    private List<Transaction> scheduledList, completedList;
    private ImageView ivBack;

    private AuthService authService;
    private DatabaseService dbService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        recyclerSchedule = findViewById(R.id.recyclerSchedule);
        recyclerCompleted = findViewById(R.id.recyclerCompleted);
        ivBack = findViewById(R.id.ivBack);

        recyclerSchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(this));

        scheduledList = new ArrayList<>();
        completedList = new ArrayList<>();

        scheduledAdapter = new TransactionAdapter(this, scheduledList);
        completedAdapter = new TransactionAdapter(this, completedList);

        recyclerSchedule.setAdapter(scheduledAdapter);
        recyclerCompleted.setAdapter(completedAdapter);

        authService = new AuthService();
        dbService = new DatabaseService();

        ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        String currentEmail = authService.getCurrentUserEmail();
        if (currentEmail == null || currentEmail.isEmpty())
        {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbService.listenUserTransactions(currentEmail, new DatabaseService.TransactionListCallback() {
            @Override
            public void onTransactionsLoaded(List<Transaction> transactions)
            {
                scheduledList.clear();
                completedList.clear();

                for (Transaction t : transactions)
                {
                    if ("Scheduled".equalsIgnoreCase(t.getStatus()))
                    {
                        scheduledList.add(t);
                    }
                    else
                    {
                        completedList.add(t);
                    }
                }

                scheduledAdapter.notifyDataSetChanged();
                completedAdapter.notifyDataSetChanged();

                if (transactions.isEmpty())
                {
                    Toast.makeText(TransactionListActivity.this, "No transactions yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error)
            {
                Toast.makeText(TransactionListActivity.this, "Error loading transactions: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}