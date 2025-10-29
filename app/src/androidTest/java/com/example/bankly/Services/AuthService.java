package com.example.bankly.Services;


import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService {
    private FirebaseAuth mAuth;

    public AuthService() {
        mAuth = FirebaseAuth.getInstance();
    }

    // Sign up new user
    public void signUpUser(String email, String password, Context context, AuthCallback callback) {
        if (password.length() < 6) {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(context, "Sign up failed: " + error, Toast.LENGTH_LONG).show();
                        callback.onFailure(error);
                    }
                });
    }

    // Sign in existing user
    public void signInUser(String email, String password, Context context, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show();
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(context, "Login failed: " + error, Toast.LENGTH_LONG).show();
                        callback.onFailure(error);
                    }
                });
    }

    // Get current user
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Get current user ID
    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Get current user email
    public String getCurrentUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    // Sign out
    public void signOut() {
        mAuth.signOut();
    }

    // Callback interface
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String error);
    }
}
