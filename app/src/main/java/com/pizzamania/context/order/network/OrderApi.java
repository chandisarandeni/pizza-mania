// java
package com.pizzamania.context.order.network;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class OrderApi {

    public interface ResponseCallback {
        void onSuccess(String responseBody);
        void onFailure(String error);
    }

    private static OkHttpClient client = new OkHttpClient();
    private static String baseUrl = "http://10.0.2.2:8080/api/v1/orders"; // override with init(...)
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static void init(String url) {
        if (url == null || url.isEmpty()) return;
        baseUrl = url.endsWith("") ? url.substring(0, url.length() - 1) : url;
    }

    public static void createOrder(String jsonBody, ResponseCallback callback) {
        String url = baseUrl + "";
        RequestBody body = RequestBody.create(jsonBody == null ? "" : jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String resp = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful() || response.code() == 201) {
                        callback.onSuccess(resp);
                    } else {
                        callback.onFailure("HTTP " + response.code() + ": " + resp);
                    }
                } catch (Exception ex) {
                    callback.onFailure("Response parse error: " + ex.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }

    public static void getOrderById(String orderId, ResponseCallback callback) {
        String url = baseUrl + "/" + orderId;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String resp = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        callback.onSuccess(resp);
                    } else {
                        callback.onFailure("HTTP " + response.code() + ": " + resp);
                    }
                } catch (Exception ex) {
                    callback.onFailure("Response parse error: " + ex.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }
}