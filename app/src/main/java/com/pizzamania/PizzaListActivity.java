package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;
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
import com.pizzamania.model.Pizza;

import java.util.ArrayList;
import java.util.List;

public class PizzaListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pizza_list);

        // Initialize views first
        drawerLayout = findViewById(R.id.drawer_layout);
        RecyclerView rv = findViewById(R.id.rv_pizzas);

        if (rv == null) {
            throw new IllegalStateException("RecyclerView with id `rv_pizzas` not found in `activity_pizza_list.xml`");
        }// adjust id
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        List<Pizza> list = new ArrayList<>();
        list.add(new Pizza(1, "Margherita", "Tomato, mozzarella", 8.50, R.drawable.pizza_placeholder));
        list.add(new Pizza(2, "Pepperoni", "Pepperoni, cheese", 9.50, R.drawable.pizza_placeholder));
        PizzaAdapter adapter = new PizzaAdapter(this, list, pizza -> {
            // Navigate to PizzaDetailsActivity with pizza data
            Intent intent = new Intent(this, PizzaDetailsActivity.class);
            intent.putExtra("pizza_name", pizza.getName());
            intent.putExtra("pizza_description", pizza.getDescription());
            intent.putExtra("pizza_price", pizza.getPrice());
            intent.putExtra("pizza_image", pizza.getImageResId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        // Only set window insets if drawer_layout exists
        if (drawerLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle menu item clicks here
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up menu icon click to open drawer
        ImageView menuIcon = findViewById(R.id.iv_menu);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set up cart icon click to open CartActivity
        ImageView cartIcon = findViewById(R.id.iv_cart);
        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
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
}