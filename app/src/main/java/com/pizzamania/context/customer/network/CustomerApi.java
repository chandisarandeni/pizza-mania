package com.pizzamania.context.customer.network;

import com.pizzamania.context.common.network.NetworkClient;

public class CustomerApi {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/customers";

    // Method : add a new customer
    public static void addCustomer(String jsonBody, NetworkClient.NetworkCallback callback) {
        NetworkClient.postRequest(BASE_URL, jsonBody, callback);
    }
}
