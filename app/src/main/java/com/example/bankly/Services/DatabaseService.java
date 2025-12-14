package com.example.bankly.Services;

import com.example.bankly.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class DatabaseService {

    private DatabaseReference database;

    public DatabaseService() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void saveUser(User user, DatabaseCallback callback) {
        database.child("users").child(user.getUid()).setValue(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User saved successfully"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getUser(String uid, UserCallback callback) {
        database.child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void findUserByEmail(String email, UserCallback callback) {
        database.child("users")
                .orderByChild("email")
                .equalTo(email)
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

    public interface DatabaseCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface UserCallback {
        void onUserLoaded(User user);
        void onUserNotFound();
        void onError(String error);
    }
}
