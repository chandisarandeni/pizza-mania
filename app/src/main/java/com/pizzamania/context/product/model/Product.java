package com.pizzamania.context.product.model;

public class Product {

    private String productId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isAvailableInColombo;
    private Boolean isAvailableInGalle;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Boolean getAvailableInColombo() {
        return isAvailableInColombo;
    }

    public void setAvailableInColombo(Boolean availableInColombo) {
        isAvailableInColombo = availableInColombo;
    }

    public Boolean getAvailableInGalle() {
        return isAvailableInGalle;
    }

    public void setAvailableInGalle(Boolean availableInGalle) {
        isAvailableInGalle = availableInGalle;
    }
}
