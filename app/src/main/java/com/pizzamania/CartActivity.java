package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.adapter.CartAdapter;
import com.pizzamania.db.CartDbHelper;
import com.pizzamania.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceText;
    private TextView emptyCartText;
    private Button checkoutButton;
    private List<CartItem> cartItems;
    private CartDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new CartDbHelper(this);
        initViews();
        setupRecyclerView();
        loadCartItems();
        updateUI();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_cart_items);
        totalPriceText = findViewById(R.id.tv_total_price);
        emptyCartText = findViewById(R.id.tv_empty_cart);
        checkoutButton = findViewById(R.id.btn_checkout);

        // Back button
        ImageView backButton = findViewById(R.id.iv_back);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Checkout button
        if (checkoutButton != null) {
            checkoutButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CheckoutActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartItems = new ArrayList<>();
            cartAdapter = new CartAdapter(cartItems);
            cartAdapter.setOnCartItemListener(this);
            recyclerView.setAdapter(cartAdapter);
        }
    }

    private void loadCartItems() {
        // Load cart items from SQLite database
        cartItems = dbHelper.getAllCartItems();
        if (cartAdapter != null) {
            cartAdapter.updateItems(cartItems);
        }
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            if (emptyCartText != null) emptyCartText.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            if (totalPriceText != null) totalPriceText.setText("Total: $0.00");
            if (checkoutButton != null) checkoutButton.setEnabled(false);
        } else {
            if (emptyCartText != null) emptyCartText.setVisibility(View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
            updateTotalPrice();
            if (checkoutButton != null) checkoutButton.setEnabled(true);
        }
    }

    private void updateTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        if (totalPriceText != null) {
            totalPriceText.setText(String.format("Total: $%.2f", total));
        }
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        // Update quantity in database
        dbHelper.updateCartItemQuantity(item.getId(), newQuantity);
        // Reload cart items
        loadCartItems();
        updateUI();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        // Remove item from database
        dbHelper.removeCartItem(item.getId());
        // Reload cart items
        loadCartItems();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload cart items when returning to this activity
        loadCartItems();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
