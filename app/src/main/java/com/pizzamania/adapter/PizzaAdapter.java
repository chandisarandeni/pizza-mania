package com.pizzamania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pizzamania.PizzaDetailsActivity;
import com.pizzamania.R;
import com.pizzamania.context.product.model.Product;

import java.util.ArrayList;
import java.util.List;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.ViewHolder> {

    private final Context context;
    private final OnItemClickListener listener;
    private List<Product> allItems;      // original full list
    private List<Product> filteredItems; // filtered list

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    // Constructor with listener
    public PizzaAdapter(Context context, List<Product> items, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.allItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
    }

    // Constructor without listener (default click opens details)
    public PizzaAdapter(List<Product> items, Context context) {
        this.context = context;
        this.listener = product -> {
            Intent intent = new Intent(context, PizzaDetailsActivity.class);
            intent.putExtra("pizza_name", product.getName());
            intent.putExtra("pizza_price", product.getPrice());
            intent.putExtra("pizza_description", product.getDescription());
            intent.putExtra("pizza_image_url", product.getImageUrl());
            intent.putExtra("product_id", product.getProductId());
            context.startActivity(intent);
        };
        this.allItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pizza, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= filteredItems.size()) return;

        Product product = filteredItems.get(position);
        holder.name.setText(product.getName() != null ? product.getName() : "Unknown Pizza");
        holder.price.setText(String.format("$%.2f", product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl() != null ? product.getImageUrl() : "")
                .apply(new RequestOptions()
                        .placeholder(R.drawable.pizza_placeholder)
                        .error(R.drawable.pizza_placeholder))
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    // Update full list and reset filter
    public void updateItems(List<Product> newItems) {
        this.allItems.clear();
        this.allItems.addAll(newItems);

        this.filteredItems.clear();
        this.filteredItems.addAll(newItems);

        notifyDataSetChanged();
    }

    // ------------------- Real-time filter -------------------
    public void filter(String query) {
        filteredItems.clear();
        if (query.isEmpty()) {
            filteredItems.addAll(allItems);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Product p : allItems) {
                if (p.getName() != null && p.getName().toLowerCase().contains(lowerQuery)) {
                    filteredItems.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView price;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_pizza);
            name = itemView.findViewById(R.id.tv_pizza_name);
            price = itemView.findViewById(R.id.tv_price);
        }
    }
}
