package com.pizzamania.context.customer.repository;

import com.pizzamania.context.customer.network.CustomerApi;
import com.pizzamania.context.common.network.NetworkClient;

public class CustomerRepository {

    public void addCustomer(String jsonBody, NetworkClient.NetworkCallback callback) {
        CustomerApi.addCustomer(jsonBody, callback);
    }

    public void getCustomerByEmail(String email, NetworkClient.NetworkCallback callback) {
        CustomerApi.getCustomerByEmail(email, callback);
    }

    // Method : update customer details
    public void updateCustomer(String customerId, String jsonBody, NetworkClient.NetworkCallback callback) {
        CustomerApi.updateCustomerByEmail(customerId, jsonBody, callback);
    }
}
