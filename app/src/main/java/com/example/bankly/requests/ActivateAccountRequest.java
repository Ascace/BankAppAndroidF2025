package com.example.bankly.requests;

public class ActivateAccountRequest {
    private String email;
    private String activationCode;

    // Constructor
    public ActivateAccountRequest(String email, String activationCode) {
        this.email = email;
        this.activationCode = activationCode;
    }

    // Empty constructor
    public ActivateAccountRequest() {
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
}