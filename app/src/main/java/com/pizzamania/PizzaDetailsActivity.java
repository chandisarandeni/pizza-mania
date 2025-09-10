package com.pizzamania;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PizzaDetailsActivity extends AppCompatActivity {

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
    }
}