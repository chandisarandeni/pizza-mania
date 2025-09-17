package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pizzamania.session.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if we need to clear pizza list and go to PizzaListActivity
        boolean clearPizzaList = getIntent().getBooleanExtra("clear_pizza_list", false);

        // ✅ Check if user is already logged in
        SessionManager session = SessionManager.getInstance(this);
        if (session.getEmail() != null && !session.getEmail().isEmpty()) {
            // User is logged in → go directly to profile/dashboard
            Intent intent = new Intent(MainActivity.this, PizzaListActivity.class);
            if (clearPizzaList) {
                intent.putExtra("clear_pizza_list", true);
            }
            startActivity(intent);
            finish(); // prevent going back to get started screen
            return;
        }

        // Otherwise show Get Started screen
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnGetStarted = findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}
