package com.example.bankly.requests;

public class TransferRequest {

    private double amount;
    private String message;
    private String account;

    public TransferRequest(double amount, String message, String account) {
        this.amount = amount;
        this.message = message;
        this.account = account;
    }

    public double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getAccount() {
        return account;
    }
}
