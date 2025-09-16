package com.pizzamania;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pizzamania.db.CartDbHelper;
import com.pizzamania.model.CartItem;

public class PizzaDetailsActivity extends AppCompatActivity {

    private TextView quantityText;
    private AppCompatImageButton btnIncrease, btnDecrease;
    private int quantity = 1;

    private TextView totalPriceText;
    private AppCompatButton btnSizeSmall, btnSizeMedium, btnSizeLarge;
    private AppCompatButton btnAddToCart;

    private double basePriceSmall = 7.99;
    private double basePriceMedium = 9.99;
    private double basePriceLarge = 12.99;
    private double currentBasePrice = basePriceSmall; // Default to small
    private String selectedSize = "Small";

    private CartDbHelper cartDbHelper;
    private String pizzaName;
    private String pizzaImageUrl; // Changed from int to String

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

        // go to cart activity
        findViewById(R.id.iv_cart_favorite).setOnClickListener(v -> {
            // Open CartActivity
            startActivity(new android.content.Intent(this, CartActivity.class));
        });
    }

    private void setupPizzaDetails() {
        // Get pizza data passed from previous activity
        pizzaName = getIntent().getStringExtra("pizza_name");
        String pizzaDescription = getIntent().getStringExtra("pizza_description");
        double pizzaPrice = getIntent().getDoubleExtra("pizza_price", 0.0);
        pizzaImageUrl = getIntent().getStringExtra("pizza_image_url"); // Fixed key name


        if (!Double.isNaN(pizzaPrice) && pizzaPrice > 0) {
            basePriceSmall = pizzaPrice;
            basePriceMedium = pizzaPrice + 2.00;
            basePriceLarge = pizzaPrice + 4.00;
            currentBasePrice = basePriceSmall;
        }

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

        // Use Glide to load image from URL
        if (pizzaImageUrl != null && imageView != null) {
            Glide.with(this)
                    .load(pizzaImageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.pizza_placeholder)
                            .error(R.drawable.pizza_placeholder))
                    .into(imageView);
        }

        // Initialize cart helper
        cartDbHelper = new CartDbHelper(this);

        quantityText = findViewById(R.id.tv_quantity);
        btnIncrease = findViewById(R.id.btn_increase_quantity);
        btnDecrease = findViewById(R.id.btn_decrease_quantity);
        totalPriceText = findViewById(R.id.tv_total_price);
        btnSizeSmall = findViewById(R.id.btn_size_small);
        btnSizeMedium = findViewById(R.id.btn_size_medium);
        btnSizeLarge = findViewById(R.id.btn_size_large);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        quantityText.setText(String.valueOf(quantity));
        updateTotalPrice();

        setupQuantityControls();
        setupSizeControls();
        setupAddToCartButton();

        // Initialize with Small size selected by default
        updateSizeButtonStates();
    }

    private void updateTotalPrice() {
        double totalPrice = currentBasePrice * quantity;
        if (totalPriceText != null) {
            totalPriceText.setText(String.format("$%.2f", totalPrice));
        }
    }

    private void setupQuantityControls() {
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
                updateTotalPrice(); // Update price when quantity changes
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
            updateTotalPrice(); // Update price when quantity changes
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
        selectedSize = size.equals("S") ? "Small" : size.equals("M") ? "Medium" : "Large";
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

    private void setupAddToCartButton() {
        btnAddToCart.setOnClickListener(v -> {
            addToCart();
        });
    }

    private void addToCart() {
        if (pizzaName == null || pizzaName.isEmpty()) {
            Toast.makeText(this, "Error: Pizza name not found", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem cartItem = new CartItem(
                pizzaName,
                selectedSize,
                quantity,
                currentBasePrice,
                pizzaImageUrl // Fixed variable name
        );

        long result = cartDbHelper.addCartItem(cartItem);

        if (result != -1) {
            Toast.makeText(this, "Added to cart successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }
}

