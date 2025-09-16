package com.pizzamania;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private LinearLayout layoutEmail, layoutOtp, layoutNewPassword;
    private ScrollView scrollView;
    private EditText etEmail, etOtp1, etOtp2, etOtp3, etOtp4, etNewPassword, etConfirmPassword;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_otp);

        scrollView = findViewById(R.id.scroll_main);
        layoutEmail = findViewById(R.id.layout_email);
        layoutOtp = findViewById(R.id.layout_otp);
        layoutNewPassword = findViewById(R.id.layout_new_password);

        etEmail = findViewById(R.id.et_email);
        etOtp1 = findViewById(R.id.et_otp_1);
        etOtp2 = findViewById(R.id.et_otp_2);
        etOtp3 = findViewById(R.id.et_otp_3);
        etOtp4 = findViewById(R.id.et_otp_4);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        // Handle system insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int originalPadding = (int) (24 * getResources().getDisplayMetrics().density);
            v.setPadding(
                    originalPadding + systemBars.left,
                    originalPadding + systemBars.top,
                    originalPadding + systemBars.right,
                    originalPadding + systemBars.bottom
            );
            return insets;
        });

        // Back arrow
        findViewById(R.id.iv_back_arrow).setOnClickListener(v -> onBackPressed());

        // Step 1: Send OTP
        findViewById(R.id.btn_send_otp).setOnClickListener(v -> sendOtp());

        // Step 2: Verify OTP
        findViewById(R.id.btn_verify_otp).setOnClickListener(v -> verifyOtp());

        // Step 3: Reset Password
        findViewById(R.id.btn_reset_password).setOnClickListener(v -> resetPassword());
    }

    // -------------------- Step 1: Send OTP --------------------
    private void sendOtp() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8080/api/v1/customers/send-otp?email=" + email;

        new Thread(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(new byte[0]))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                final String resp = response.body() != null ? response.body().string() : "No response";
                runOnUiThread(() -> {
                    Toast.makeText(ChangePasswordActivity.this, resp, Toast.LENGTH_SHORT).show();

                    // Show OTP layout
                    layoutEmail.setVisibility(View.GONE);
                    layoutOtp.setVisibility(View.VISIBLE);
                    scrollView.post(() -> scrollView.scrollTo(0, layoutOtp.getTop()));
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this,
                        "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // -------------------- Step 2: Verify OTP --------------------
    private void verifyOtp() {
        String otp = etOtp1.getText().toString().trim() +
                etOtp2.getText().toString().trim() +
                etOtp3.getText().toString().trim() +
                etOtp4.getText().toString().trim();

        if (otp.length() != 4) {
            Toast.makeText(this, "Enter complete 4-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etEmail.getText().toString().trim();
        String url = "http://10.0.2.2:8080/api/v1/customers/verify-otp?email=" + email + "&otp=" + otp;

        new Thread(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                final String resp = response.body() != null ? response.body().string() : "No response";
                runOnUiThread(() -> {
                    if (resp.contains("success")) {
                        Toast.makeText(ChangePasswordActivity.this, "OTP verified", Toast.LENGTH_SHORT).show();

                        // Show new password layout
                        layoutOtp.setVisibility(View.GONE);
                        layoutNewPassword.setVisibility(View.VISIBLE);
                        scrollView.post(() -> scrollView.scrollTo(0, layoutNewPassword.getTop()));
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, resp, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this,
                        "Failed to verify OTP: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // -------------------- Step 3: Reset Password --------------------
    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Enter both password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etEmail.getText().toString().trim();
        String url = "http://10.0.2.2:8080/api/v1/customers/reset-password" +
                "?email=" + email + "&newPassword=" + newPassword;

        new Thread(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(new byte[0])) // empty body
                    .build();

            try (Response response = client.newCall(request).execute()) {
                final String resp = response.body() != null ? response.body().string() : "No response";
                runOnUiThread(() -> {
                    Toast.makeText(ChangePasswordActivity.this, resp, Toast.LENGTH_SHORT).show();
                    if (resp.contains("success")) {
                        finish(); // go back to login
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this,
                        "Failed to reset password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
