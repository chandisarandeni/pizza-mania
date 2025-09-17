package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.adapter.CartAdapter;
import com.pizzamania.context.order.repository.OrderRepository;
import com.pizzamania.db.CartDbHelper;
import com.pizzamania.model.CartItem;
import com.pizzamania.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
                // Check if we're coming from checkout (payment already validated)
                boolean fromCheckout = getIntent().getBooleanExtra("from_checkout", false);

                if (fromCheckout) {
                    // Place the order directly since payment is already validated
                    placeOrder();
                } else {
                    // Normal flow - go to checkout first
                    Intent intent = new Intent(this, CheckoutActivity.class);
                    // Pass the actual numeric total value instead of the formatted text
                    double totalAmount = calculateTotalPrice();
                    intent.putExtra("total_price", totalAmount);
                    try {
                       org.json.JSONArray cartArray = new org.json.JSONArray();
                       for(CartItem ci : cartItems) {
                           org.json.JSONObject obj = new org.json.JSONObject();
                           obj.put("id", ci.getId());
                           obj.put("price", ci.getPrice());
                           obj.put("quantity", ci.getQuantity());
                           obj.put("name", ci.getName());
                           obj.put("size", ci.getSize());
                           cartArray.put(obj);
                       }
                       intent.putExtra("cart_items_json", cartArray.toString());
                    }catch(Exception e) {
                        intent.putExtra("cart_items_json", "[]");
                    }
                    startActivity(intent);
                }
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

    private double calculateTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    private void updateTotalPrice() {
        double total = calculateTotalPrice();
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

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get customer info from session
        SessionManager session = SessionManager.getInstance(this);
        String customerId = session.getCustomerId();
        String customerName = session.getName();
        String customerPhone = session.getPhone();
        String customerEmail = session.getEmail();

        if (customerId == null || customerId.isEmpty()) {
            Toast.makeText(this, "Customer ID missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (customerName == null || customerName.isEmpty()) {
            Toast.makeText(this, "Customer name missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (customerPhone == null || customerPhone.isEmpty()) {
            Toast.makeText(this, "Customer phone missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (customerEmail == null || customerEmail.isEmpty()) {
            Toast.makeText(this, "Customer email missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total amount
        double totalAmount = calculateTotalPrice();

        // Build order
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("customerId", customerId);
        orderMap.put("email", customerEmail);
        orderMap.put("customerName", customerName);
        orderMap.put("customerPhone", customerPhone);
        orderMap.put("branchId", "BRANCH_001"); // Default branch
        orderMap.put("orderAmount", totalAmount);
        orderMap.put("orderStatus", "CREATED");

        // Format orderDate
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String orderDate = sdf.format(new Date());
        orderMap.put("orderDate", orderDate);

        // Build items array
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", String.valueOf(item.getId()));
            map.put("productName", item.getName());
            map.put("productSize", item.getSize());
            map.put("quantity", item.getQuantity());
            map.put("price", item.getPrice());
            itemsList.add(map);
        }
        orderMap.put("items", itemsList);

        // Add payment info (payment already validated in CheckoutActivity)
        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("method", "card");
        paymentMap.put("card_last4", "4242"); // Last 4 digits of valid card
        paymentMap.put("authorized", true);
        orderMap.put("payment", paymentMap);

        // Place the order
        OrderRepository orderRepository = new OrderRepository();
        orderRepository.createOrderFromMap(orderMap, new OrderRepository.RepoCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    // Navigate to checkout success screen
                    Intent successIntent = new Intent(CartActivity.this, CheckoutSuccessActivity.class);
                    successIntent.putExtra("total_amount", totalAmount);
                    successIntent.putExtra("order_date", orderDate);
                    startActivity(successIntent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Navigate to checkout failure screen
                    Intent failureIntent = new Intent(CartActivity.this, CheckoutFailureActivity.class);
                    failureIntent.putExtra("error_message", error);
                    failureIntent.putExtra("total_amount", totalAmount);
                    startActivity(failureIntent);
                    finish();
                });
            }
        });

        // Show loading message
        Toast.makeText(this, "Placing your order...", Toast.LENGTH_SHORT).show();
    }
}
