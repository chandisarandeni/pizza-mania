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
import com.pizzamania.model.Pizza;

import java.util.List;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Pizza pizza);
    }

    private List<Pizza> items;
    private final Context context;
    private final OnItemClickListener listener;

    public PizzaAdapter(Context context, List<Pizza> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    // Alternative constructor for simpler usage
    public PizzaAdapter(List<Pizza> items, Context context) {
        this.items = items;
        this.context = context;
        this.listener = pizza -> {
            // Default click behavior - open pizza details
            Intent intent = new Intent(context, PizzaDetailsActivity.class);
            intent.putExtra("pizza_name", pizza.getName());
            intent.putExtra("pizza_price", pizza.getPrice());
            intent.putExtra("pizza_description", pizza.getDescription());
            intent.putExtra("pizza_image_url", pizza.getImageUrl());
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
        Pizza pizza = items.get(position);

        // Safely set text only if views exist
        if (holder.name != null) {
            holder.name.setText(pizza.getName());
        }

        if (holder.price != null) {
            holder.price.setText(String.format("$%.2f", pizza.getPrice()));
        }

        // Load image with Glide
        if (holder.image != null) {
            Glide.with(context)
                    .load(pizza.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.pizza_placeholder)
                            .error(R.drawable.pizza_placeholder))
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(pizza);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    // Add updateItems method for refreshing data
    public void updateItems(List<Pizza> newItems) {
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
            // Removed description since it doesn't exist in layout
        }
    }
}