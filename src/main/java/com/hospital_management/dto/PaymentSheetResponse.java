package com.hospital_management.dto;



public class PaymentSheetResponse {
    private String paymentIntent;
    private String ephemeralKey;
    private String customer;
    private String publishableKey;

    public PaymentSheetResponse(String paymentIntent, String ephemeralKey, String customer, String publishableKey) {
        this.paymentIntent = paymentIntent;
        this.ephemeralKey = ephemeralKey;
        this.customer = customer;
        this.publishableKey = publishableKey;
    }

    // Getters
    public String getPaymentIntent() {
        return paymentIntent;
    }

    public String getEphemeralKey() {
        return ephemeralKey;
    }

    public String getCustomer() {
        return customer;
    }

    public String getPublishableKey() {
        return publishableKey;
    }
}
