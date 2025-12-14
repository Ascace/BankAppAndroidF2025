package com.example.bankly.Models;

import com.example.bankly.Models.Account;
import java.io.Serializable;

public class Transaction implements Serializable {

    private double amount;
    private String dateTime;
    private Account fromAccount;
    private String message;
    private Account toAccount;
    private String transactionId;
    private String type;

    public double getAmount() { return amount; }
    public String getDateTime() { return dateTime; }
    public Account getFromAccount() { return fromAccount; }
    public String getMessage() { return message; }
    public Account getToAccount() { return toAccount; }
    public String getTransactionId() { return transactionId; }
    public String getType() { return type; }
}
