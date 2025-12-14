package com.example.bankly.requests;

public class DepositRequest {

    private double amount;
    private String message;
    private String email;

    public DepositRequest(double amount, String message, String email) {
        this.amount = amount;
        this.message = message;
        this.email = email;
    }

    public double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }
}
