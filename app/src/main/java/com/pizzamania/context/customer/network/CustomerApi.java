package com.pizzamania.context.customer.network;

import com.pizzamania.context.common.network.NetworkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomerApi {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/customers";

    // Method: add a new customer
    public static void addCustomer(String jsonBody, NetworkClient.NetworkCallback callback) {
        NetworkClient.postRequest(BASE_URL, jsonBody, callback);
    }

    // Method: get customer by email
    public static void getCustomerByEmail(String email, NetworkClient.NetworkCallback callback) {
        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());
            String url = BASE_URL + "/get-by-email?email=" + encodedEmail;
            NetworkClient.getRequest(url, callback);
        } catch (UnsupportedEncodingException e) {
            callback.onFailure(e);
        }
    }

    // Method: update customer details by email
    public static void updateCustomerByEmail(String email, String jsonBody, NetworkClient.NetworkCallback callback) {
        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());
            String url = BASE_URL + "/update-by-email/" + encodedEmail;
            NetworkClient.putRequest(url, jsonBody, callback);
        } catch (UnsupportedEncodingException e) {
            callback.onFailure(e);
        }
    }
}
