package com.pizzamania.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.model.Pizza;

import java.util.List;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Pizza pizza);
    }

    private final List<Pizza> items;
    private final Context context;
    private final OnItemClickListener listener;

    public PizzaAdapter(Context context, List<Pizza> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
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
        holder.name.setText(pizza.getName());
        holder.price.setText(String.format("$%.2f", pizza.getPrice()));
        holder.image.setImageResource(pizza.getImageResId());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(pizza);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        TextView price;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_pizza);
            name = itemView.findViewById(R.id.tv_pizza_name);
            description = itemView.findViewById(R.id.tv_pizza_description);
            price = itemView.findViewById(R.id.tv_price);
        }
    }
}