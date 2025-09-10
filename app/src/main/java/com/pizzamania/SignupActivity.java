package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;
import com.pizzamania.context.common.network.NetworkClient;
import com.pizzamania.context.customer.repository.CustomerRepository;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private AppCompatEditText etEmail, etPassword, etConfirmPassword;
    private AppCompatButton btnCreateAccount;
    private CustomerRepository repository;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge layout
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnCreateAccount = findViewById(R.id.btn_create_account);

        repository = new CustomerRepository();
        gson = new Gson();

        // TextWatcher to allow lowercase letters, numbers, @ and .
        etEmail.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                isUpdating = true;
                // Keep lowercase letters, numbers, @ and .
                String filtered = s.toString().toLowerCase().replaceAll("[^a-z0-9@.]", "");
                if (!s.toString().equals(filtered)) {
                    etEmail.setText(filtered);
                    etEmail.setSelection(filtered.length());
                }
                isUpdating = false;
            }
        });


        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Login link
        findViewById(R.id.tv_login_link).setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class))
        );

        // Create Account click
        btnCreateAccount.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            // Basic validation
            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build JSON using Gson
            HashMap<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("password", password);
            String jsonBody = gson.toJson(data);

            // Call repository to add customer
            repository.addCustomer(jsonBody, new NetworkClient.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        if (response.contains("Error: Email already exists")) {
                            Toast.makeText(SignupActivity.this, "Email already exists! Please use a different one.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                            // Optionally, you could pass OTP or customerId to next screen if needed
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(SignupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            });
        });

    }
}
