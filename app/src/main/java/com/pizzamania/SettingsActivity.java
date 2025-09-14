package com.pizzamania;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Back button
        AppCompatImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> onBackPressed());
    }
}
