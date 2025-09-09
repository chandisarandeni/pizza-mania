package com.pizzamania.model;

public class Pizza {
    private final int id;
    private final String name;
    private final String description;
    private final double price;
    private final int imageResId;

    public Pizza(int id, String name, String description, double price, int imageResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public double getPrice() {
        return price;
    }
    public int getImageResId() {
        return imageResId;
    }
}
