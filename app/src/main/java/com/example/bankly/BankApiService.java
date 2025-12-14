package com.example.bankly;

import com.example.bankly.requests.ActivateAccountRequest;
import com.example.bankly.requests.CreateAccountRequest;
import com.example.bankly.requests.LoginRequest;
import com.example.bankly.responses.AccountInfoResponse;
import com.example.bankly.responses.AuthResponse;
import com.example.bankly.responses.BalanceResponse;
import com.example.bankly.requests.DepositRequest;
import com.example.bankly.requests.WithdrawRequest;
import com.example.bankly.requests.TransferRequest;
import com.example.bankly.responses.ApiErrorResponse;
import com.example.bankly.Models.Transaction;
import com.example.bankly.responses.TransactionHistoryResponse;
import com.example.bankly.responses.TransactionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BankApiService {

    // 1. Create Account
    @POST("/v1/accounts/create")
    Call<AuthResponse> createAccount(@Body CreateAccountRequest request);

    // 2. Login
    @POST("/v1/accounts/auth")
    Call<AuthResponse> login(@Body LoginRequest request);

    // 3. Activate Account
    @POST("/v1/accounts/activate")
    Call<AuthResponse> activateAccount(@Body ActivateAccountRequest request);

    // 4. Get Account Info
    @GET("/v1/accounts/info")
    Call<AccountInfoResponse> getAccountInfo();

    // 5. Get Account Info by Email
    @GET("/v1/accounts/info/{email}")
    Call<AccountInfoResponse> getAccountInfoByEmail(@Path("email") String email);

    // 6. Get Balance
    @GET("/v1/accounts/balance")
    Call<BalanceResponse> getBalance();

    // 7. Post Deposit
    @POST("/v1/transactions/deposit")
    Call<TransactionResponse> deposit(@Body DepositRequest request);


    // 8. POST Withdraw
    @POST("/v1/transactions/withdraw")
    Call<ApiErrorResponse> withdraw(@Body WithdrawRequest request);

    // 9. POST Transfer Funds
    @POST("/v1/transactions/transfer/")
    Call<ApiErrorResponse> transfer(@Body TransferRequest request);

    // 10. Transaction History
    @GET("/v1/transactions/{days}/statement")
    Call<TransactionHistoryResponse> getTransactions(@Path("days") int days);

}