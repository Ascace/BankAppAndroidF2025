package com.example.bankly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.requests.WithdrawRequest;
import com.example.bankly.responses.ApiErrorResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    private EditText etAmount;
    private Button btnWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        etAmount = findViewById(R.id.et_amount);
        btnWithdraw = findViewById(R.id.btn_withdraw);

        btnWithdraw.setOnClickListener(v -> {
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

            performWithdraw(amount);
        });
    }

    private void performWithdraw(double amount) {

        WithdrawRequest request = new WithdrawRequest(amount);

        RetrofitClient.getInstanceWithAuth(this)
                .create(BankApiService.class)
                .withdraw(request)
                .enqueue(new Callback<ApiErrorResponse>() {
                    @Override
                    public void onResponse(Call<ApiErrorResponse> call, Response<ApiErrorResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(WithdrawActivity.this,
                                    "Withdrawal successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            handleError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiErrorResponse> call, Throwable t) {
                        Toast.makeText(WithdrawActivity.this,
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
                    : "Withdrawal failed";

            Toast.makeText(WithdrawActivity.this, errorMsg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(WithdrawActivity.this, "Withdrawal failed!", Toast.LENGTH_LONG).show();
        }
    }
}