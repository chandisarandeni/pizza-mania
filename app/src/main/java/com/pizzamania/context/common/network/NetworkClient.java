package com.pizzamania.context.common.network;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class NetworkClient {

    private static final OkHttpClient client = new OkHttpClient();

    // GET request
    public static void getRequest(String url, NetworkCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        executeRequest(request, callback);
    }

    // POST request
    public static void postRequest(String url, String jsonBody, NetworkCallback callback) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        executeRequest(request, callback);
    }

    // PUT request
    public static void putRequest(String url, String jsonBody, NetworkCallback callback) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        executeRequest(request, callback);
    }

    // DELETE request
    public static void deleteRequest(String url, NetworkCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        executeRequest(request, callback);
    }

    // Execute request
    private static void executeRequest(Request request, NetworkCallback callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Unexpected code " + response));
                } else {
                    callback.onSuccess(response.body().string());
                }
            }
        });
    }

    // Callback interface
    public interface NetworkCallback {
        void onSuccess(String response);

        void onFailure(Exception e);
    }
}
