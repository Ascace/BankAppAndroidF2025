package com.example.bankly.Models;

import java.io.Serializable;

public class Transaction implements Serializable {

    private String transactionId;
    private String senderId;
    private String senderEmail;
    private String recipientEmail;
    private double amount;
    private String message;
    private String status;
    private long timestamp;
    private boolean confirmedByRecipient;

    public Transaction() {
    }

    public Transaction(String senderId, String senderEmail, String recipientEmail,
                       double amount, String message, String status, long timestamp)
    {
        this.transactionId = String.valueOf(System.currentTimeMillis());
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.amount = amount;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.confirmedByRecipient = false;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isConfirmedByRecipient() {
        return confirmedByRecipient;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setConfirmedByRecipient(boolean confirmedByRecipient) {
        this.confirmedByRecipient = confirmedByRecipient;
    }
}