package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.pizzamania.context.common.network.NetworkClient;
import com.pizzamania.context.customer.model.Customer;
import com.pizzamania.context.customer.repository.CustomerRepository;
import com.pizzamania.session.SessionManager;
import com.pizzamania.utils.PasswordUtils; // 🔹 import hashing utility

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private CustomerRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Auto-login if already logged in
        if (SessionManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, PizzaListActivity.class));
            finish();
            return;
        }

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        repository = new CustomerRepository();

        setupEmailFilter();
        setupLoginButton();
        setupNavigationButtons();
    }

    private void setupEmailFilter() {
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
                String filtered = s.toString().toLowerCase().replaceAll("[^a-z0-9@.]", "");
                if (!s.toString().equals(filtered)) {
                    etEmail.setText(filtered);
                    etEmail.setSelection(filtered.length());
                }
                isUpdating = false;
            }
        });
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);

            repository.getCustomerByEmail(email, new NetworkClient.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> btnLogin.setEnabled(true));
                    try {
                        Gson gson = new Gson();
                        Customer[] customers = gson.fromJson(response, Customer[].class);

                        if (customers.length > 0) {
                            Customer customer = customers[0];
                            String backendPassword = customer.getPassword() != null ? customer.getPassword().trim() : "";

                            // 🔹 Hash the entered password before comparing
                            String hashedInputPassword = PasswordUtils.hashPasswordSHA256(password);

                            if (hashedInputPassword.equals(backendPassword)) {
                                // Save user session including customerId
                                SessionManager.getInstance(LoginActivity.this).saveUser(
                                        customer.getEmail(),
                                        customer.getName() != null ? customer.getName() : "",
                                        customer.getPhone() != null ? customer.getPhone() : "",
                                        customer.getAddress() != null ? customer.getAddress() : "",
                                        customer.getCustomerId()
                                );

                                runOnUiThread(() -> {
                                    Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, PizzaListActivity.class));
                                    finish();
                                });
                                return;
                            }
                        }

                        // Invalid login
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            etPassword.setText("");
                            btnLogin.setEnabled(true);
                            btnLogin.performHapticFeedback(1);
                            etPassword.requestFocus();
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    e.printStackTrace();
                }
            });
        });
    }

    private void setupNavigationButtons() {
        findViewById(R.id.btn_signup).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        findViewById(R.id.tv_forgot).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordOtpActivity.class)));
    }
}
