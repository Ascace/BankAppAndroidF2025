package com.example.bankly.Models;

public class User {
    private String uid;
    private String email;
    private double balance;
    private long createdAt;
    private long lastUpdated;


    public User() {
    }

    // Constructor
    public User(String uid, String email, double balance) {
        this.uid = uid;
        this.email = email;
        this.balance = balance;
        this.createdAt = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        this.lastUpdated = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}