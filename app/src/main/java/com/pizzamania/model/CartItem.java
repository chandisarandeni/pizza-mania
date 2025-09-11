package com.pizzamania.model;

public class CartItem {
    private long id;
    private String name;
    private String size;
    private int quantity;
    private double price;
    private int imageRes;

    public CartItem() {}

    public CartItem(String name, String size, int quantity, double price, int imageRes) {
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.imageRes = imageRes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public int getImageRes() { return imageRes; }

    public void setName(String name) { this.name = name; }
    public void setSize(String size) { this.size = size; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }
}
