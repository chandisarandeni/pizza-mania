package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import com.pizzamania.session.SessionManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back button
        AppCompatImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> onBackPressed());

        // Logout button
        AppCompatButton logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(v -> {
            // Clear session
            SessionManager.getInstance(this).clear();

            // Navigate to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // navigate to Privacy Policy
        AppCompatButton privacyPolicyBtn = findViewById(R.id.btn_privacy_policy);
        privacyPolicyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });
    }
}
