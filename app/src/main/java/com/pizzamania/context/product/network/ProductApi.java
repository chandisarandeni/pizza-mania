package com.pizzamania.context.product.network;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pizzamania.context.product.model.Product;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProductApi {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/products";
    private final OkHttpClient client;
    private final Gson gson;
    private final Handler mainHandler;

    public ProductApi() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void fetchProductsAsync(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage() != null ? e.getMessage() : "network error"));
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    String msg = "HTTP " + response.code();
                    mainHandler.post(() -> callback.onError(msg));
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    mainHandler.post(() -> callback.onError("Empty response body"));
                    return;
                }
                try {
                    String json = body.string();
                    Type listType = new TypeToken<List<Product>>() {}.getType();
                    List<Product> products = gson.fromJson(json, listType);
                    mainHandler.post(() -> callback.onSuccess(products));
                } catch (IOException ex) {
                    mainHandler.post(() -> callback.onError(ex.getMessage() != null ? ex.getMessage() : "parse error"));
                } finally {
                    body.close();
                }
            }
        });
    }

    public interface Callback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }
}