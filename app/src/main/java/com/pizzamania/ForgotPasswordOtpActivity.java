package com.pizzamania;

import android.os.Bundle;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordOtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Preserve the original XML padding (24dp) and add system bar insets
            int originalPadding = (int) (24 * getResources().getDisplayMetrics().density); // Convert 24dp to pixels
            v.setPadding(
                originalPadding + systemBars.left,
                originalPadding + systemBars.top,
                originalPadding + systemBars.right,
                originalPadding + systemBars.bottom
            );
            return insets;
        });

        // login page navigation
        findViewById(R.id.iv_back_arrow).setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordOtpActivity.this, LoginActivity.class));
        });
    }
}