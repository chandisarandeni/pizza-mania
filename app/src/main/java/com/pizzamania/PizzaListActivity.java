package com.pizzamania;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.adapter.PizzaAdapter;
import com.pizzamania.model.Pizza;

import java.util.ArrayList;
import java.util.List;

public class PizzaListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pizza_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView rv = findViewById(R.id.rv_pizzas);

        if (rv == null) {
            throw new IllegalStateException("RecyclerView with id `rv_pizzas` not found in `activity_pizza_list.xml`");
        }// adjust id
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        List<Pizza> list = new ArrayList<>();
        list.add(new Pizza(1, "Margherita", "Tomato, mozzarella", 8.50, R.drawable.pizza_placeholder));
        list.add(new Pizza(2, "Pepperoni", "Pepperoni, cheese", 9.50, R.drawable.pizza_placeholder));
        PizzaAdapter adapter = new PizzaAdapter(this, list, pizza -> {
            // handle click
        });
        rv.setAdapter(adapter);
    }
}