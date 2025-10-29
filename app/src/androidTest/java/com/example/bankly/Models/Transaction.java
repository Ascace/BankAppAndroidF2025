package com.example.bankly.Models;


public class Transaction {
    private String transactionId;
    private String senderId;
    private String senderEmail;
    private String recipientEmail;
    private double amount;
    private String message;
    private String type;
    private String status;
    private long scheduledDate;
    private long processingDate;
    private boolean confirmedByRecipient;


    public Transaction() {
    }

    // Constructor
    public Transaction(String senderId, String senderEmail, String recipientEmail,
                       double amount, String message, String type, long scheduledDate) {
        this.transactionId = java.util.UUID.randomUUID().toString();
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.amount = amount;
        this.message = message;
        this.type = type;
        this.status = "Scheduled";
        this.scheduledDate = scheduledDate;
        this.processingDate = 0;
        this.confirmedByRecipient = false;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(long scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public long getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(long processingDate) {
        this.processingDate = processingDate;
    }

    public boolean isConfirmedByRecipient() {
        return confirmedByRecipient;
    }

    public void setConfirmedByRecipient(boolean confirmedByRecipient) {
        this.confirmedByRecipient = confirmedByRecipient;
    }
}
