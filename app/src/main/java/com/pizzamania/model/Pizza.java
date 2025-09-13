package com.pizzamania.model;

public class Pizza {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private boolean isAvailable;
    private boolean isAvailableInColombo;
    private boolean isAvailableInGalle;

    public Pizza( String name, String description, double price, String imageUrl, boolean isAvailable, boolean isAvailableInColombo, boolean isAvailableInGalle) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.isAvailableInColombo = isAvailableInColombo;
        this.isAvailableInGalle = isAvailableInGalle;
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
    public String getImageUrl() {
        return imageUrl;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public boolean isAvailableInColombo() {
        return isAvailableInColombo;
    }
    public boolean isAvailableInGalle() {
        return isAvailableInGalle;
    }

}
