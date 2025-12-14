package com.example.bankly.responses;

import com.example.bankly.Models.Transaction;
import java.util.List;

public class TransactionHistoryResponse {

    private List<Transaction> statement;

    public List<Transaction> getStatement() {
        return statement;
    }
}

