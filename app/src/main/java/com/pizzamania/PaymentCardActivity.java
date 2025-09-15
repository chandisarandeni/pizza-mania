package com.pizzamania;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card);

        // Back arrow click
        ImageView ivBack = findViewById(R.id.iv_back_arrow);
        ivBack.setOnClickListener(v -> onBackPressed());

        // Add New Card button
        findViewById(R.id.btn_add_card).setOnClickListener(v ->
                Toast.makeText(this, "Add New Card Clicked", Toast.LENGTH_SHORT).show()
        );
    }
}
