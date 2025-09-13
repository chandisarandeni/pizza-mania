package com.pizzamania.api;

import com.pizzamania.model.Pizza;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiServices {

    @GET("api/v1/products")
    Call<List<Pizza>> getPizzas();
}
