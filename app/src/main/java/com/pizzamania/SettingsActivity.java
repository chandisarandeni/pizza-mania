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

        // navigate to change password
        AppCompatButton changePasswordBtn = findViewById(R.id.btn_change_password);
        changePasswordBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordOtpActivity.class);
            startActivity(intent);
        });

        // navigate to edit profile
        AppCompatButton editProfileBtn = findViewById(R.id.btn_edit_profile);
        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyProfileActivity.class);
            startActivity(intent);
        });

        // navigate to Privacy Policy
        AppCompatButton privacyPolicyBtn = findViewById(R.id.btn_privacy_policy);
        privacyPolicyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        // navigate to Terms and Conditions
        AppCompatButton termsBtn = findViewById(R.id.btn_terms);
        termsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsConditionsActivity.class);
            startActivity(intent);
        });

        // navigate to Notification settings
        AppCompatButton notificationBtn = findViewById(R.id.btn_notifications);
        notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        });
    }
}
