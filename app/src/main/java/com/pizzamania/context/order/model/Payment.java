package com.pizzamania.context.order.model;

public class Payment {
    private String method;
    private String card_last4;
    private boolean authorized;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCard_last4() {
        return card_last4;
    }

    public void setCard_last4(String card_last4) {
        this.card_last4 = card_last4;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
