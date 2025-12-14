package com.example.bankly.Models;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountId;
    private String email;
    private String firstName;
    private String lastName;
    private String mobilePhone;

    public String getEmail() { return email; }
    public String getAccountId() { return accountId; }
}


