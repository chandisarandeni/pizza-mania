package com.pizzamania.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pizzamania.R;
import com.pizzamania.model.CartItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
    private final List<CartItem> items;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(List<CartItem> items) {
        this.items = items;
    }

    public void setOnCartItemListener(OnCartItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.size.setText(String.format("Size: %s", item.getSize()));
        holder.quantity.setText(String.format("Qty: %d", item.getQuantity()));
        holder.price.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice() * item.getQuantity()));
        String imageUrl = item.getImageRes();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.image.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.pizza_placeholder)
                    .error(R.drawable.pizza_placeholder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.pizza_placeholder);
        }
        holder.quantityDisplay.setText(String.valueOf(item.getQuantity()));

        // Set click listeners
        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() + 1);
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (listener != null && item.getQuantity() > 1) {
                listener.onQuantityChanged(item, item.getQuantity() - 1);
            } else if (listener != null && item.getQuantity() == 1) {
                listener.onItemRemoved(item);
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateItems(List<CartItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, size, quantity, price, quantityDisplay;
        ImageButton btnIncrease, btnDecrease;

        VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_cart_image);
            name = itemView.findViewById(R.id.tv_cart_name);
            size = itemView.findViewById(R.id.tv_cart_size);
            quantity = itemView.findViewById(R.id.tv_cart_quantity);
            price = itemView.findViewById(R.id.tv_cart_price);
            quantityDisplay = itemView.findViewById(R.id.tv_quantity_display);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}