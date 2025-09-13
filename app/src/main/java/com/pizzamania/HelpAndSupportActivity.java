package com.pizzamania;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HelpAndSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support); // Use your XML layout

        // --- Edge-to-edge handling ---
        ScrollView scrollView = findViewById(R.id.scroll_help);
        if (scrollView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // --- Back button ---
        ImageView backBtn = findViewById(R.id.iv_back_arrow);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }
    }
}
