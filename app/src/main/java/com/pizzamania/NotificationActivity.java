package com.pizzamania;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private View emptyState;
    private List<String> notifications; // simple example, can replace with Notification model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back arrow click
        ImageView ivBack = findViewById(R.id.iv_back_arrow);
        ivBack.setOnClickListener(v -> onBackPressed());

        // Find views
        rvNotifications = findViewById(R.id.rv_notifications);
        emptyState = findViewById(R.id.ll_empty_state);

        // Example notifications list
        notifications = new ArrayList<>(); // leave empty to show empty state
        // notifications.add("Order #123 delivered"); // uncomment to test with data

        if (notifications.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            rvNotifications.setLayoutManager(new LinearLayoutManager(this));
            // TODO: set your NotificationAdapter here
            // rvNotifications.setAdapter(new NotificationAdapter(notifications));
        }
    }
}
