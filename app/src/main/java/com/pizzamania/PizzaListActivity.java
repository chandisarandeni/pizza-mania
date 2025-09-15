package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.pizzamania.adapter.PizzaAdapter;
import com.pizzamania.context.product.model.Product;
import com.pizzamania.context.product.repository.ProductRepository;
import com.pizzamania.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PizzaListActivity extends AppCompatActivity {

    private static final String TAG = "PizzaListActivity";

    private DrawerLayout drawerLayout;
    private RecyclerView rv;
    private PizzaAdapter adapter;
    private ProductRepository productRepository;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pizza_list);

        productRepository = new ProductRepository();

        drawerLayout = findViewById(R.id.drawer_layout);
        rv = findViewById(R.id.rv_pizzas);
        navigationView = findViewById(R.id.nav_view);

        setupWindowInsets();
        setupDrawer();
        setupRecyclerView();
        setupMenuAndCart();

        handleSessionUser();

        fetchProducts();
    }

    private void setupWindowInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupDrawer() {
        if (navigationView != null && drawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(item -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });

            setupNavigationMenu();
        }
    }

    private void setupNavigationMenu() {
        if (navigationView == null) return;

        navigationView.getMenu().findItem(R.id.nav_help_support).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, HelpAndSupportActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
            SessionManager.getInstance(this).clear();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_profile).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, MyProfileActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_settings).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, SettingsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_order_history).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupRecyclerView() {
        if (rv != null) {
            rv.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new PizzaAdapter(new ArrayList<>(), this);
            rv.setAdapter(adapter);
        }
    }

    private void setupMenuAndCart() {
        ImageView menuIcon = findViewById(R.id.iv_menu);
        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        ImageView cartIcon = findViewById(R.id.iv_cart);
        if (cartIcon != null) {
            cartIcon.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        }
    }

    private void handleSessionUser() {
        SessionManager session = SessionManager.getInstance(this);
        String email = session.getEmail();
        if (email != null && !email.isEmpty()) {
            Toast.makeText(this, "Logged in as: " + email, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProducts() {
        Log.d(TAG, "Fetching products...");

        productRepository.getProducts(new ProductRepository.ProductsCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Fetched " + products.size() + " products");
                    adapter.updateItems(products);
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to fetch products: " + error);
                runOnUiThread(PizzaListActivity.this::loadSampleProducts);
            }
        });
    }

    private void loadSampleProducts() {
        List<Product> sampleProducts = new ArrayList<>();

        Product margherita = new Product();
        margherita.setProductId("1");
        margherita.setName("Margherita");
        margherita.setDescription("Classic pizza");
        margherita.setPrice(12.99);
        margherita.setImageUrl("https://example.com/margherita.jpg");

        Product pepperoni = new Product();
        pepperoni.setProductId("2");
        pepperoni.setName("Pepperoni");
        pepperoni.setDescription("Pepperoni pizza");
        pepperoni.setPrice(15.99);
        pepperoni.setImageUrl("https://example.com/pepperoni.jpg");

        Product veggie = new Product();
        veggie.setProductId("3");
        veggie.setName("Veggie");
        veggie.setDescription("Veggie pizza");
        veggie.setPrice(14.99);
        veggie.setImageUrl("https://example.com/veggie.jpg");

        sampleProducts.add(margherita);
        sampleProducts.add(pepperoni);
        sampleProducts.add(veggie);

        adapter.updateItems(sampleProducts);
    }
}
