// java
package com.pizzamania.context.order.repository;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.pizzamania.context.order.network.OrderApi;

import java.util.Map;

public class OrderRepository {

    public interface RepoCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    private final Gson gson = new Gson();

    public void createOrder(@NonNull String jsonBody, @NonNull RepoCallback callback) {
        OrderApi.createOrder(jsonBody, new OrderApi.ResponseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                callback.onSuccess(responseBody);
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        });
    }

    public void createOrderFromMap(@NonNull Map<String, Object> orderData, @NonNull RepoCallback callback) {
        try {
            String json = gson.toJson(orderData);
            createOrder(json, callback);
        } catch (Exception ex) {
            callback.onError("invalid_order_data: " + ex.getMessage());
        }
    }

    public void getOrderById(@NonNull String orderId, @NonNull RepoCallback callback) {
        OrderApi.getOrderById(orderId, new OrderApi.ResponseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                callback.onSuccess(responseBody);
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        });
    }

    public void getOrdersByEmail(@NonNull String email, @NonNull RepoCallback callback) {
        OrderApi.getOrdersByEmail(email, new OrderApi.ResponseCallback() {
            @Override
            public void onSuccess(String responseBody) {
                callback.onSuccess(responseBody);
            }

            @Override
            public void onFailure(String error) {
                callback.onError(error);
            }
        });
    }

}