package com.pizzamania;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PizzaDetailsActivity extends AppCompatActivity {

    private TextView quantityText;
    private AppCompatImageButton btnIncrease, btnDecrease;
    private int quantity = 1;

    private TextView totalPriceText;
    private AppCompatButton btnSizeSmall, btnSizeMedium, btnSizeLarge;

    private double basePriceSmall = 7.99;
    private double basePriceMedium = 9.99;
    private double basePriceLarge = 12.99;
    private double currentBasePrice = 7.99; // Default to small
    private String selectedSize = "S";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pizza_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get pizza data from intent
        if (getIntent() != null) {
            setupPizzaDetails();
        }

        // Set up back button
        findViewById(R.id.iv_back_arrow).setOnClickListener(v -> finish());
    }

    private void setupPizzaDetails() {
        // Get pizza data passed from previous activity
        String pizzaName = getIntent().getStringExtra("pizza_name");
        String pizzaDescription = getIntent().getStringExtra("pizza_description");
        double pizzaPrice = getIntent().getDoubleExtra("pizza_price", 0.0);
        int pizzaImageId = getIntent().getIntExtra("pizza_image", R.drawable.pizza_placeholder);

        // Update UI with pizza data
        TextView titleView = findViewById(R.id.tv_pizza_title);
        TextView descriptionView = findViewById(R.id.tv_pizza_description);
        ImageView imageView = findViewById(R.id.iv_pizza_image);

        if (pizzaName != null) {
            titleView.setText(pizzaName);
        }
        if (pizzaDescription != null) {
            descriptionView.setText(pizzaDescription);
        }
        imageView.setImageResource(pizzaImageId);

        // You can use the price for additional functionality later
        // For example, updating a price TextView if you add one to the layout

        quantityText = findViewById(R.id.tv_quantity);
        btnIncrease = findViewById(R.id.btn_increase_quantity);
        btnDecrease = findViewById(R.id.btn_decrease_quantity);
        totalPriceText = findViewById(R.id.tv_total_price);
        btnSizeSmall = findViewById(R.id.btn_size_small);
        btnSizeMedium = findViewById(R.id.btn_size_medium);
        btnSizeLarge = findViewById(R.id.btn_size_large);

        quantityText.setText(String.valueOf(quantity));
        updateTotalPrice();

        quantityText.setText(String.valueOf(quantity));

        setupQuantityControls();
        setupSizeControls();
    }

    private void updateTotalPrice() {
        double totalPrice = currentBasePrice * quantity;
        totalPriceText.setText(String.format("$%.2f", totalPrice));
    }

    private void setupQuantityControls() {
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });
    }


    private void setupSizeControls() {
        btnSizeSmall.setOnClickListener(v -> {
            selectSize("S", basePriceSmall);
            updateSizeButtonStates();
        });

        btnSizeMedium.setOnClickListener(v -> {
            selectSize("M", basePriceMedium);
            updateSizeButtonStates();
        });

        btnSizeLarge.setOnClickListener(v -> {
            selectSize("L", basePriceLarge);
            updateSizeButtonStates();
        });
    }

    private void selectSize(String size, double price) {
        selectedSize = size;
        currentBasePrice = price;
        updateTotalPrice();
    }

    private void updateSizeButtonStates() {
        // Reset all buttons to unselected state
        btnSizeSmall.setBackgroundResource(R.drawable.size_button_unselected);
        btnSizeMedium.setBackgroundResource(R.drawable.size_button_unselected);
        btnSizeLarge.setBackgroundResource(R.drawable.size_button_unselected);

        btnSizeSmall.setTextColor(getResources().getColor(android.R.color.darker_gray));
        btnSizeMedium.setTextColor(getResources().getColor(android.R.color.darker_gray));
        btnSizeLarge.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Set selected button to selected state
        switch (selectedSize) {
            case "S":
                btnSizeSmall.setBackgroundResource(R.drawable.size_button_selected);
                btnSizeSmall.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "M":
                btnSizeMedium.setBackgroundResource(R.drawable.size_button_selected);
                btnSizeMedium.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "L":
                btnSizeLarge.setBackgroundResource(R.drawable.size_button_selected);
                btnSizeLarge.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }


    }
}