package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
import com.pizzamania.api.ApiServices;
import com.pizzamania.api.RetrofitClient;
import com.pizzamania.model.Pizza;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PizzaListActivity extends AppCompatActivity {

    private static final String TAG = "PizzaListActivity";
    private DrawerLayout drawerLayout;
    private RecyclerView rv;
    private PizzaAdapter adapter;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pizza_list);

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

        // Fetch pizzas from API
        fetchPizzas();
    }

    private void fetchPizzas() {
        ApiServices api = RetrofitClient.getInstance().create(ApiServices.class);
        Call<List<Pizza>> call = api.getPizzas();
        call.enqueue(new Callback<List<Pizza>>() {
            @Override
            public void onResponse(Call<List<Pizza>> call, Response<List<Pizza>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pizza> pizzas = response.body();
                    if (adapter != null) {
                        adapter.updateItems(pizzas);
                    }
                } else {
                    Log.e(TAG, "API response empty or failed: " + response.code());
                    // For testing, load sample data if API fails
                    loadSamplePizzas();
                }
            }

            @Override
            public void onFailure(Call<List<Pizza>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                // For testing, load sample data if API fails
                loadSamplePizzas();
            }
        });

        // Help and support click listener
        navigationView.getMenu().findItem(R.id.nav_help_support).setOnMenuItemClickListener(item -> {
            // Navigate to HelpAndSupportActivity
            Intent intent = new Intent(this, HelpAndSupportActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // set logout click listener
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
            // Handle logout action
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
    }

    private void loadSamplePizzas() {
        // Load sample pizzas for testing
        List<Pizza> samplePizzas = new ArrayList<>();
        samplePizzas.add(new Pizza("Margherita", "Classic pizza with tomato and mozzarella", 12.99, "", true, true, true));
        samplePizzas.add(new Pizza("Pepperoni", "Pepperoni with mozzarella cheese", 14.99, "", true, true, true));
        samplePizzas.add(new Pizza("Hawaiian", "Ham and pineapple with cheese", 15.99, "", true, true, false));
        samplePizzas.add(new Pizza("Meat Lovers", "Pepperoni, sausage, and ham", 17.99, "", true, true, true));
        samplePizzas.add(new Pizza("Veggie Supreme", "Fresh vegetables and cheese", 16.99, "", true, false, true));
        samplePizzas.add(new Pizza("BBQ Chicken", "BBQ sauce with grilled chicken", 16.99, "", true, true, true));

        if (adapter != null) {
            adapter.updateItems(samplePizzas);
        }
    }

}