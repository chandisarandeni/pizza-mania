package com.pizzamania;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pizzamania.context.common.network.NetworkClient;
import com.pizzamania.context.customer.network.CustomerApi;
import com.pizzamania.session.SessionManager;

public class MyProfileActivity extends AppCompatActivity {

    private TextView tvNameValue, tvEmailValue, tvPhoneValue, tvAddressValue;
    private Button btnEdit, btnSave;
    private EditText etName, etEmail, etPhone, etAddress;
    private LinearLayout llProfileContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialize views
        tvNameValue = findViewById(R.id.tv_name_value);
        tvEmailValue = findViewById(R.id.tv_email_value);
        tvPhoneValue = findViewById(R.id.tv_phone_value);
        tvAddressValue = findViewById(R.id.tv_address_value);

        btnEdit = findViewById(R.id.btn_edit_profile);
        btnSave = findViewById(R.id.btn_save_profile);

        llProfileContainer = findViewById(R.id.ll_profile_container);

        // Initialize EditTexts
        etName = new EditText(this);
        etEmail = new EditText(this);
        etPhone = new EditText(this);
        etAddress = new EditText(this);

        loadProfile();

        btnEdit.setOnClickListener(v -> enableEditMode());
        btnSave.setOnClickListener(v -> saveProfile());

        // navigation back
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
    }

    private void loadProfile() {
        SessionManager session = SessionManager.getInstance(this);
        tvNameValue.setText(session.getName() != null ? session.getName() : "N/A");
        tvEmailValue.setText(session.getEmail() != null ? session.getEmail() : "N/A");
        tvPhoneValue.setText(session.getPhone() != null ? session.getPhone() : "N/A");
        tvAddressValue.setText(session.getAddress() != null ? session.getAddress() : "N/A");
    }

    private void enableEditMode() {
        etName.setText(tvNameValue.getText());
        etEmail.setText(tvEmailValue.getText());
        etPhone.setText(tvPhoneValue.getText());
        etAddress.setText(tvAddressValue.getText());

        tvNameValue.setVisibility(View.GONE);
        tvEmailValue.setVisibility(View.GONE);
        tvPhoneValue.setVisibility(View.GONE);
        tvAddressValue.setVisibility(View.GONE);

        addEditTextAfter(tvNameValue, etName);
        addEditTextAfter(tvEmailValue, etEmail);
        addEditTextAfter(tvPhoneValue, etPhone);
        addEditTextAfter(tvAddressValue, etAddress);

        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
    }

    private void addEditTextAfter(TextView tv, EditText et) {
        LinearLayout parent = (LinearLayout) tv.getParent();
        et.setLayoutParams(tv.getLayoutParams());
        parent.addView(et, parent.indexOfChild(tv) + 1);
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare JSON for backend (email is not changed)
        String jsonBody = "{"
                + "\"name\":\"" + name + "\","
                + "\"email\":\"" + email + "\","
                + "\"phone\":\"" + phone + "\","
                + "\"address\":\"" + address + "\""
                + "}";

        // Use email-based update
        CustomerApi.updateCustomerByEmail(email, jsonBody, new NetworkClient.NetworkCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    // Update session
                    SessionManager.getInstance(MyProfileActivity.this)
                            .saveUser(email, name, phone, address,
                                    SessionManager.getInstance(MyProfileActivity.this).getCustomerId());

                    // Update UI
                    tvNameValue.setText(name);
                    tvEmailValue.setText(email);
                    tvPhoneValue.setText(phone);
                    tvAddressValue.setText(address);

                    tvNameValue.setVisibility(View.VISIBLE);
                    tvEmailValue.setVisibility(View.VISIBLE);
                    tvPhoneValue.setVisibility(View.VISIBLE);
                    tvAddressValue.setVisibility(View.VISIBLE);

                    removeEditText(etName);
                    removeEditText(etEmail);
                    removeEditText(etPhone);
                    removeEditText(etAddress);

                    btnEdit.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);

                    Toast.makeText(MyProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(MyProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void removeEditText(EditText et) {
        LinearLayout parent = (LinearLayout) et.getParent();
        if (parent != null) parent.removeView(et);
    }
}
