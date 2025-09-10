package com.pizzamania.context.customer.model;

import java.util.Date;

public class Customer {
    private String customerId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String password;
    private int otpCode;
    private Date otpGeneratedTime;

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(int otpCode) {
        this.otpCode = otpCode;
    }

    public Date getOtpGeneratedTime() {
        return otpGeneratedTime;
    }

    public void setOtpGeneratedTime(Date otpGeneratedTime) {
        this.otpGeneratedTime = otpGeneratedTime;
    }
}
