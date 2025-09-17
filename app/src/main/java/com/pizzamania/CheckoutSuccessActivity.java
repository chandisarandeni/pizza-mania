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

import com.pizzamania.db.CartDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckoutSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();
        setupButtons();
    }

    private void setupUI() {
        // Get order details from intent
        double totalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        String orderDate = getIntent().getStringExtra("order_date");

        // Set total amount
        TextView totalAmountText = findViewById(R.id.tv_total_amount);
        if (totalAmountText != null) {
            totalAmountText.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount));
        }

        // Set order date - format it nicely for display
        TextView dateText = findViewById(R.id.tv_date);
        if (dateText != null && orderDate != null) {
            try {
                // Parse the ISO date and format it nicely
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
                Date date = isoFormat.parse(orderDate);
                String formattedDate = displayFormat.format(date != null ? date : new Date());
                dateText.setText(formattedDate);
            } catch (Exception e) {
                // Fallback to current date if parsing fails
                SimpleDateFormat fallbackFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
                dateText.setText(fallbackFormat.format(new Date()));
            }
        }

        // Setup back button
        ImageView backButton = findViewById(R.id.iv_back_arrow);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void setupButtons() {
        // See Order Details button - clears cart and goes to order history
        Button seeOrderDetailsButton = findViewById(R.id.btn_see_order_details);
        if (seeOrderDetailsButton != null) {
            seeOrderDetailsButton.setOnClickListener(v -> {
                // Clear the cart from database
                CartDbHelper cartDbHelper = new CartDbHelper(CheckoutSuccessActivity.this);
                cartDbHelper.clearCart();
                cartDbHelper.close();

                // Navigate to order history
                Intent intent = new Intent(CheckoutSuccessActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // Home button - clears cart and pizza list, goes to main activity
        Button homeButton = findViewById(R.id.btn_home);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                // Clear the cart from database
                CartDbHelper cartDbHelper = new CartDbHelper(CheckoutSuccessActivity.this);
                cartDbHelper.clearCart();
                cartDbHelper.close();

                // Navigate to main activity and clear the back stack
                Intent intent = new Intent(CheckoutSuccessActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("clear_pizza_list", true); // Signal to clear pizza list
                startActivity(intent);
                finish();
            });
        }
    }
}