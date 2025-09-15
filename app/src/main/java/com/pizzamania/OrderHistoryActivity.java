package com.pizzamania;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pizzamania.adapter.OrderAdapter;
import com.pizzamania.context.order.repository.OrderRepository;
import com.pizzamania.session.SessionManager;

import java.lang.reflect.Type;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private LinearLayout llEmptyState;
    private OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Edge-to-edge padding handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvOrderHistory = findViewById(R.id.rv_order_history);
        llEmptyState = findViewById(R.id.ll_empty_state);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        orderRepository = new OrderRepository();

        // ✅ Fetch email from session
        String email = SessionManager.getInstance(this).getEmail();

        if (email != null) {
            fetchOrders(email);
        } else {
            showEmptyState();
        }
    }

    private void fetchOrders(String email) {
        orderRepository.getOrdersByEmail(email, new OrderRepository.RepoCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<com.pizzamania.context.order.model.Order>>() {
                    }.getType();
                    List<com.pizzamania.context.order.model.Order> orders = gson.fromJson(response, listType);

                    if (orders == null || orders.isEmpty()) {
                        showEmptyState();
                    } else {
                        llEmptyState.setVisibility(View.GONE);
                        rvOrderHistory.setVisibility(View.VISIBLE);
                        rvOrderHistory.setAdapter(new OrderAdapter(orders));
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderHistoryActivity.this, error, Toast.LENGTH_SHORT).show();
                    showEmptyState();
                });
            }
        });
    }

    private void showEmptyState() {
        rvOrderHistory.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
    }
}
