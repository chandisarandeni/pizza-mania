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

import java.util.List;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private List<Product> items;
    private final Context context;
    private final OnItemClickListener listener;

    public PizzaAdapter(Context context, List<Product> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    // Alternative constructor for simpler usage
    public PizzaAdapter(List<Product> items, Context context) {
        this.items = items;
        this.context = context;
        this.listener = product -> {
            // Default click behavior - open pizza details
            Intent intent = new Intent(context, PizzaDetailsActivity.class);
            intent.putExtra("pizza_name", product.getName());
            intent.putExtra("pizza_price", product.getPrice());
            intent.putExtra("pizza_description", product.getDescription());
            intent.putExtra("pizza_image_url", product.getImageUrl());
            intent.putExtra("product_id", product.getProductId());
            context.startActivity(intent);
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pizza, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (items == null || position >= items.size()) {
            return;
        }

        Product product = items.get(position);

        // Safely set text only if views exist and product is not null
        if (product != null && holder.name != null) {
            holder.name.setText(product.getName() != null ? product.getName() : "Unknown Pizza");
        }

        if (product != null && holder.price != null) {
            holder.price.setText(String.format("$%.2f", product.getPrice()));
        }

        // Load image with Glide
        if (product != null && holder.image != null) {
            String imageUrl = product.getImageUrl();
            Glide.with(context)
                    .load(imageUrl != null ? imageUrl : "")
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.pizza_placeholder)
                            .error(R.drawable.pizza_placeholder))
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && product != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    // Add updateItems method for refreshing data
    public void updateItems(List<Product> newItems) {
        this.items = newItems;
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