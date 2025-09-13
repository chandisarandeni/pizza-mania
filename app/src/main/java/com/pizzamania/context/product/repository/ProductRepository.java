package com.pizzamania.context.product.repository;

import com.pizzamania.context.product.model.Product;
import com.pizzamania.context.product.network.ProductApi;

import java.util.List;

public class ProductRepository {

    private final ProductApi api;

    public ProductRepository() {
        this.api = new ProductApi();
    }

    public void getProducts(ProductsCallback callback) {
        api.fetchProductsAsync(new ProductApi.Callback() {
            @Override
            public void onSuccess(List<Product> products) {
                callback.onSuccess(products);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public interface ProductsCallback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }
}