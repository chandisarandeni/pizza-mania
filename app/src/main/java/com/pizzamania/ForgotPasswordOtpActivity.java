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

public class ForgotPasswordOtpActivity extends AppCompatActivity {

    private LinearLayout layoutEmail, layoutOtp, layoutNewPassword;
    private ScrollView scrollView;
    private EditText etEmail, etOtp1, etOtp2, etOtp3, etOtp4, etNewPassword, etConfirmPassword;

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

        // Back arrow & button
        findViewById(R.id.iv_back_arrow).setOnClickListener(v -> onBackPressed());

        // Step 1: Send OTP
        findViewById(R.id.btn_send_otp).setOnClickListener(v -> sendOtp());

        // Step 2: Verify OTP
        findViewById(R.id.btn_verify_otp).setOnClickListener(v -> verifyOtp());

        // Step 3: Reset Password
        findViewById(R.id.btn_reset_password).setOnClickListener(v -> resetPassword());
    }

    private void sendOtp() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Call your API to send OTP here

        Toast.makeText(this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();

        // Show OTP layout
        layoutEmail.setVisibility(View.GONE);
        layoutOtp.setVisibility(View.VISIBLE);
        scrollView.post(() -> scrollView.scrollTo(0, layoutOtp.getTop()));
    }

    private void verifyOtp() {
        String otp = etOtp1.getText().toString().trim() +
                etOtp2.getText().toString().trim() +
                etOtp3.getText().toString().trim() +
                etOtp4.getText().toString().trim();

        if (otp.length() != 4) {
            Toast.makeText(this, "Enter complete 4-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Call your API to verify OTP here

        Toast.makeText(this, "OTP verified", Toast.LENGTH_SHORT).show();

        // Show new password layout
        layoutOtp.setVisibility(View.GONE);
        layoutNewPassword.setVisibility(View.VISIBLE);
        scrollView.post(() -> scrollView.scrollTo(0, layoutNewPassword.getTop()));
    }

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

        // TODO: Call your API to reset password here

        Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();

        // Optionally finish activity and go back to login
        finish();
    }
}
