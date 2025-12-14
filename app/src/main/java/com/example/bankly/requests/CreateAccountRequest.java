package com.example.bankly.requests;

public class CreateAccountRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String mobilePhone;
    private String password;

    // Constructor
    public CreateAccountRequest(String firstName, String lastName, String email,
                                String mobilePhone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.password = password;
    }

    // Empty constructor (required by Gson)
    public CreateAccountRequest() {
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}