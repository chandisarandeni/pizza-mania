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

        // Initialize repository
        productRepository = new ProductRepository();

        // Initialize views first
        drawerLayout = findViewById(R.id.drawer_layout);
        rv = findViewById(R.id.rv_pizzas);

        // Only set window insets if main view exists, fallback to drawer_layout
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else if (drawerLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null && drawerLayout != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(item -> {
                // Handle menu item clicks here
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        // Set up RecyclerView
        if (rv != null) {
            rv.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new PizzaAdapter(new ArrayList<>(), this);
            rv.setAdapter(adapter);
        }

        // Set up menu icon click to open drawer
        ImageView menuIcon = findViewById(R.id.iv_menu);
        if (menuIcon != null && drawerLayout != null) {
            menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        // Set up cart icon click to open CartActivity
        ImageView cartIcon = findViewById(R.id.iv_cart);
        if (cartIcon != null) {
            cartIcon.setOnClickListener(v -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            });
        }

        // Set up navigation menu items
        setupNavigationMenu();

        // Fetch products from API using OkHttp
        fetchProducts();

        //show email data
        String email = SessionManager.getInstance(this).getEmail();
        if(email != null && !email.isEmpty()) {
            Toast.makeText(this, "Logged in as: " + email, Toast.LENGTH_SHORT).show();
        }
        else {
            SessionManager sessionManager = SessionManager.getInstance(this);
            sessionManager.createFakeUser();
            sessionManager.saveEmail(sessionManager.getEmail());
            Toast.makeText(this, "No user found, created a fake user." + sessionManager.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNavigationMenu() {
        if (navigationView == null) return;

        // Help and support click listener
        navigationView.getMenu().findItem(R.id.nav_help_support).setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(this, HelpAndSupportActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Logout click listener
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
    }

    private void fetchProducts() {
        Log.d(TAG, "Fetching products using OkHttp...");

        productRepository.getProducts(new ProductRepository.ProductsCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                Log.d(TAG, "Successfully fetched " + products.size() + " products");
                if (adapter != null) {
                    adapter.updateItems(products);
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to fetch products: " + error);
                // Load sample data as fallback
                loadSampleProducts();
            }
        });
    }

    private void loadSampleProducts() {
        Log.d(TAG, "Loading sample products as fallback");
        List<Product> sampleProducts = createSampleProducts();
        if (adapter != null) {
            adapter.updateItems(sampleProducts);
        }
    }

    private List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();

        Product margherita = new Product();
        margherita.setProductId("1");
        margherita.setName("Margherita");
        margherita.setDescription("Classic pizza with tomato sauce, mozzarella, and fresh basil");
        margherita.setPrice(12.99);
        margherita.setImageUrl("https://example.com/margherita.jpg");
        margherita.setAvailable(true);
        margherita.setAvailableInColombo(true);
        margherita.setAvailableInGalle(true);

        Product pepperoni = new Product();
        pepperoni.setProductId("2");
        pepperoni.setName("Pepperoni");
        pepperoni.setDescription("Delicious pizza with pepperoni and melted cheese");
        pepperoni.setPrice(15.99);
        pepperoni.setImageUrl("https://example.com/pepperoni.jpg");
        pepperoni.setAvailable(true);
        pepperoni.setAvailableInColombo(true);
        pepperoni.setAvailableInGalle(false);

        Product veggie = new Product();
        veggie.setProductId("3");
        veggie.setName("Veggie Supreme");
        veggie.setDescription("Loaded with fresh vegetables and cheese");
        veggie.setPrice(14.99);
        veggie.setImageUrl("https://example.com/veggie.jpg");
        veggie.setAvailable(true);
        veggie.setAvailableInColombo(true);
        veggie.setAvailableInGalle(true);

        Product hawaiian = new Product();
        hawaiian.setProductId("4");
        hawaiian.setName("Hawaiian");
        hawaiian.setDescription("Ham and pineapple with cheese");
        hawaiian.setPrice(13.99);
        hawaiian.setImageUrl("https://example.com/hawaiian.jpg");
        hawaiian.setAvailable(true);
        hawaiian.setAvailableInColombo(true);
        hawaiian.setAvailableInGalle(true);

        products.add(margherita);
        products.add(pepperoni);
        products.add(veggie);
        products.add(hawaiian);

        return products;
    }
}