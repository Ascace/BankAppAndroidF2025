package com.example.bankly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.Services.AuthService;
import com.example.bankly.requests.DepositRequest;
import com.example.bankly.responses.ApiErrorResponse;
import com.example.bankly.responses.TransactionResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private EditText etAmount;
    private Button btnDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        etAmount = findViewById(R.id.et_amount);
        btnDeposit = findViewById(R.id.btn_deposit);

        btnDeposit.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();

            if (amountStr.isEmpty()) {
                etAmount.setError("Amount is required");
                etAmount.requestFocus();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                etAmount.setError("Enter a valid number");
                etAmount.requestFocus();
                return;
            }

            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                etAmount.requestFocus();
                return;
            }

            performDeposit(amount);
        });
    }

    private void performDeposit(double amount) {

        String email = new AuthService().getCurrentUserEmail();
        String message = "Deposit";

        DepositRequest request = new DepositRequest(amount, message, email);

        RetrofitClient.getInstanceWithAuth(this)
                .create(BankApiService.class)
                .deposit(request)
                .enqueue(new Callback<TransactionResponse>() {
                    @Override
                    public void onResponse(Call<TransactionResponse> call,
                                           Response<TransactionResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(DepositActivity.this,
                                    "Deposit successful! ID: " + response.body().getTransactionId(),
                                    Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            Toast.makeText(DepositActivity.this,
                                    "Deposit failed. Try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
                        Toast.makeText(DepositActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleError(Response<ApiErrorResponse> response) {
        try {
            Gson gson = new Gson();
            ApiErrorResponse errorResponse = gson.fromJson(
                    response.errorBody().charStream(),
                    ApiErrorResponse.class
            );

            String errorMsg = (errorResponse != null && errorResponse.getError() != null)
                    ? errorResponse.getError()
                    : "Deposit failed";

            Toast.makeText(DepositActivity.this, errorMsg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(DepositActivity.this, "Deposit failed!", Toast.LENGTH_LONG).show();
        }
    }
}