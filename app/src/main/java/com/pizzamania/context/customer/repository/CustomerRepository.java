package com.pizzamania.context.customer.repository;

import com.pizzamania.context.customer.network.CustomerApi;
import com.pizzamania.context.common.network.NetworkClient;

public class CustomerRepository {

    public void addCustomer(String jsonBody, NetworkClient.NetworkCallback callback) {
        CustomerApi.addCustomer(jsonBody, callback);
    }
}
