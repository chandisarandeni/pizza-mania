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
import com.pizzamania.session.SessionManager;
import com.pizzamania.utils.PasswordUtils; // 🔹 import utility

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private AppCompatEditText etEmail, etPassword, etConfirmPassword, etName;
    private AppCompatButton btnCreateAccount;
    private CustomerRepository repository;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnCreateAccount = findViewById(R.id.btn_create_account);

        repository = new CustomerRepository();
        gson = new Gson();

        // Email TextWatcher to filter characters
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

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Login link
        findViewById(R.id.tv_login_link).setOnClickListener(v ->
                startActivity(new Intent(SignupActivity.this, LoginActivity.class))
        );

        // Create Account
        btnCreateAccount.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔹 Hash the password before sending
            String hashedPassword = PasswordUtils.hashPasswordSHA256(password);

            // Build JSON
            HashMap<String, String> data = new HashMap<>();
            data.put("name", name);
            data.put("email", email);
            data.put("password", hashedPassword);

            String jsonBody = gson.toJson(data);

            repository.addCustomer(jsonBody, new NetworkClient.NetworkCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        if (response.contains("Error: Email already exists")) {
                            Toast.makeText(SignupActivity.this, "Email already exists!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                            // Save session temporarily
                            SessionManager session = SessionManager.getInstance(SignupActivity.this);
                            session.saveUser(email, name, "", "", "");

                            // **Instant logout**
                            session.clear();

                            // Send welcome email (optional)
                            sendWelcomeEmail(email, name, true);
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

    /**
     * Send welcome email.
     */
    private void sendWelcomeEmail(String email, String name, boolean navigateToLogin) {
        if (email == null || email.isEmpty()) {
            if (navigateToLogin) goToLogin();
            return;
        }

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                HashMap<String, String> bodyMap = new HashMap<>();
                bodyMap.put("email", email);
                bodyMap.put("name", name != null && !name.isEmpty() ? name : "Customer");

                String jsonBody = gson.toJson(bodyMap);

                RequestBody requestBody = RequestBody.create(
                        jsonBody,
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url("http://10.0.2.2:3000/api/v1/notifications/send-welcome")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "Welcome email sent!", Toast.LENGTH_SHORT).show();
                        if (navigateToLogin) goToLogin();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(SignupActivity.this, "Failed to send welcome email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (navigateToLogin) goToLogin();
                });
            }
        }).start();
    }

    private void goToLogin() {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }
}
