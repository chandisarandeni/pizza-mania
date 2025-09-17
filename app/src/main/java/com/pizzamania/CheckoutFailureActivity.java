package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class CheckoutFailureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout_failure);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
        setupButtons();
    }

    private void setupUI() {
        // Get error details from intent
        String errorMessage = getIntent().getStringExtra("error_message");
        double totalAmount = getIntent().getDoubleExtra("total_amount", 0.0);

        // Set error message
        TextView errorText = findViewById(R.id.tv_error_message);
        if (errorText != null && errorMessage != null) {
            errorText.setText(errorMessage);
        }

        // Set total amount if available
        TextView totalAmountText = findViewById(R.id.tv_total_amount);
        if (totalAmountText != null) {
            totalAmountText.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount));
        }

        // Setup back button
        ImageView backButton = findViewById(R.id.iv_back_arrow);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void setupButtons() {
        // Try Again button - goes back to cart
        Button tryAgainButton = findViewById(R.id.btn_try_again);
        if (tryAgainButton != null) {
            tryAgainButton.setOnClickListener(v -> {
                Intent intent = new Intent(CheckoutFailureActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // Home button - goes to main pizza list
        Button homeButton = findViewById(R.id.btn_home);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(CheckoutFailureActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}