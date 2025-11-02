package com.example.bankly.Services;


import com.example.bankly.Models.Transaction;
import com.example.bankly.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;


import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private DatabaseReference database;

    public DatabaseService() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    // Create or update user
    public void saveUser(User user, DatabaseCallback callback) {
        database.child("users").child(user.getUid()).setValue(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User saved successfully"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get user by ID
    public void getUser(String uid, UserCallback callback) {
        database.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    callback.onUserLoaded(user);
                } else {
                    callback.onUserNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Create transaction
    public void createTransaction(Transaction transaction, DatabaseCallback callback) {
        database.child("transactions").child(transaction.getTransactionId()).setValue(transaction)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Transaction created successfully"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get all transactions for a user
    public void getUserTransactions(String userEmail, TransactionListCallback callback) {
        database.child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> transactions = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Transaction transaction = child.getValue(Transaction.class);
                    if (transaction != null &&
                            (transaction.getSenderEmail().equals(userEmail) ||
                                    transaction.getRecipientEmail().equals(userEmail))) {
                        transactions.add(transaction);
                    }
                }
                callback.onTransactionsLoaded(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Update transaction statu
    public void updateTransactionStatus(String transactionId, String status, DatabaseCallback callback) {
        database.child("transactions").child(transactionId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Status updated"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Confirm transaction
    public void confirmTransaction(String transactionId, DatabaseCallback callback) {
        database.child("transactions").child(transactionId).child("confirmedByRecipient").setValue(true)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Transaction confirmed"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Update user balance
    public void updateUserBalance(String uid, double newBalance, DatabaseCallback callback) {
        database.child("users").child(uid).child("balance").setValue(newBalance)
                .addOnSuccessListener(aVoid -> {
                    database.child("users").child(uid).child("lastUpdated").setValue(System.currentTimeMillis());
                    callback.onSuccess("Balance updated");
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public interface DatabaseCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface UserCallback {
        void onUserLoaded(User user);
        void onUserNotFound();
        void onError(String error);
    }

    public interface TransactionListCallback {
        void onTransactionsLoaded(List<Transaction> transactions);
        void onError(String error);
    }



    // Find user by email
    public void findUserByEmail(String email, UserCallback callback) {
        database.child("users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                User user = child.getValue(User.class);
                                callback.onUserLoaded(user);
                                return;
                            }
                        }
                        callback.onUserNotFound();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }


    public void listenUserTransactions(String userEmail, TransactionListCallback callback) {
        database.child("transactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> transactions = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Transaction transaction = child.getValue(Transaction.class);
                    if (transaction != null &&
                            (transaction.getSenderEmail().equals(userEmail) ||
                                    transaction.getRecipientEmail().equals(userEmail))) {
                        transactions.add(transaction);
                    }
                }
                callback.onTransactionsLoaded(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Complete transaction and update s
    public void completeTransactionAndUpdateBalances(Transaction transaction, User receiver, User sender, DatabaseCallback callback) {
        double amount = transaction.getAmount();

        // Update sender balance (subtract)
        sender.setBalance(sender.getBalance() - amount);

        // Update receiver balance (add)
        receiver.setBalance(receiver.getBalance() + amount);

        // Update transaction status
        transaction.setStatus("Completed");
        transaction.setConfirmedByRecipient(true);
        // ❌ Removed transaction.setProcessingDate() because it doesn't exist anymore

        database.child("users").child(sender.getUid()).setValue(sender)
                .addOnSuccessListener(aVoid -> {
                    database.child("users").child(receiver.getUid()).setValue(receiver)
                            .addOnSuccessListener(aVoid2 -> {
                                database.child("transactions").child(transaction.getTransactionId()).setValue(transaction)
                                        .addOnSuccessListener(aVoid3 -> callback.onSuccess("Transaction completed successfully"))
                                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                            })
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

}