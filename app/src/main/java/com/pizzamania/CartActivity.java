package com.pizzamania;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.adapter.CartAdapter;
import com.pizzamania.db.CartDbHelper;
import com.pizzamania.model.CartItem;

import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private CartDbHelper dbHelper;
    private RecyclerView rvCart;
    private CartAdapter adapter;
    private LinearLayout llEmptyCart, llCheckoutSection;
    private TextView tvSubtotal, tvTotal, tvClearCart;
    private Button btnBrowsePizzas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadCart();
    }

    private void initViews() {
        dbHelper = new CartDbHelper(this);
        rvCart = findViewById(R.id.rv_cart_items);
        llEmptyCart = findViewById(R.id.ll_empty_cart);
        llCheckoutSection = findViewById(R.id.ll_checkout_section);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotal = findViewById(R.id.tv_total);
        tvClearCart = findViewById(R.id.tv_clear_cart);
        btnBrowsePizzas = findViewById(R.id.btn_browse_pizzas);
    }

    private void setupRecyclerView() {
        rvCart.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        tvClearCart.setOnClickListener(v -> clearCart());
        btnBrowsePizzas.setOnClickListener(v -> finish());

        findViewById(R.id.iv_back_arrow).setOnClickListener(v -> finish());
    }

    private void loadCart() {
        List<CartItem> items = dbHelper.getAllCartItems();

        if (items.isEmpty()) {
            showEmptyState();
        } else {
            showCartContent(items);
        }
    }

    private void showEmptyState() {
        rvCart.setVisibility(View.GONE);
        llCheckoutSection.setVisibility(View.GONE);
        llEmptyCart.setVisibility(View.VISIBLE);
        tvClearCart.setVisibility(View.GONE);
    }

    private void showCartContent(List<CartItem> items) {
        rvCart.setVisibility(View.VISIBLE);
        llCheckoutSection.setVisibility(View.VISIBLE);
        llEmptyCart.setVisibility(View.GONE);
        tvClearCart.setVisibility(View.VISIBLE);

        if (adapter == null) {
            adapter = new CartAdapter(items);
            adapter.setOnCartItemListener(this);
            rvCart.setAdapter(adapter);
        } else {
            adapter.updateItems(items);
        }

        updateTotals();
    }

    private void updateTotals() {
        double subtotal = dbHelper.getTotalPrice();
        double deliveryFee = 2.99;
        double total = subtotal + deliveryFee;

        tvSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", subtotal));
        tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", total));
    }

    private void clearCart() {
        dbHelper.clearCart();
        loadCart();
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        dbHelper.updateCartItemQuantity(item.getId(), newQuantity);
        loadCart();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        dbHelper.removeCartItem(item.getId());
        loadCart();
    }
}