package com.example.bankly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankly.requests.TransferRequest;
import com.example.bankly.responses.ApiErrorResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTransactionActivity extends AppCompatActivity {

    private EditText etEmail, etAmount, etMessage;
    private CalendarView calendarView;
    private Button btnSendMoney;

    private long selectedDateTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        etEmail = findViewById(R.id.etEmail);
        etAmount = findViewById(R.id.et_amount);
        etMessage = findViewById(R.id.et_message);
        calendarView = findViewById(R.id.calendarView);
        btnSendMoney = findViewById(R.id.btn_send_money);


        selectedDateTimestamp = calendarView.getDate();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDateTimestamp = calendar.getTimeInMillis();
        });

        btnSendMoney.setOnClickListener(v -> validateAndSend());
    }

    private void validateAndSend() {
        String recipient = etEmail.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        // Validate recipient email
        if (recipient.isEmpty()) {
            etEmail.setError("Recipient email required");
            etEmail.requestFocus();
            return;
        }

        // Validate amount
        if (amountStr.isEmpty()) {
            etAmount.setError("Amount required");
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

        sendMoney(recipient, amount, message);
    }

    private void sendMoney(String recipientEmail, double amount, String message) {

        TransferRequest request = new TransferRequest(amount, message, recipientEmail);

        RetrofitClient.getInstanceWithAuth(this)
                .create(BankApiService.class)
                .transfer(request)
                .enqueue(new Callback<ApiErrorResponse>() {
                    @Override
                    public void onResponse(Call<ApiErrorResponse> call, Response<ApiErrorResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateTransactionActivity.this,
                                    "Money sent successfully!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            handleError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiErrorResponse> call, Throwable t) {
                        Toast.makeText(CreateTransactionActivity.this,
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
                    : "Transfer failed";

            Toast.makeText(CreateTransactionActivity.this, errorMsg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(CreateTransactionActivity.this,
                    "Transfer failed!",
                    Toast.LENGTH_LONG).show();
        }
    }
}