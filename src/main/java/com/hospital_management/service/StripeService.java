package com.hospital_management.service;



import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.hospital_management.dto.PaymentSheetRequest;
import com.hospital_management.dto.PaymentSheetResponse;
import com.stripe.exception.StripeException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    @Value("${stripe.api.version}")
    private String stripeApiVersion;   // 👈  STRIPE_API_VERSION=2025-06-30.basil

    public PaymentSheetResponse createPaymentSheetParams(PaymentSheetRequest request) throws StripeException {
        // 1. Create customer
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setDescription("Customer for payment")
                .build();
        Customer customer = Customer.create(customerParams);

        // 2. Create ephemeral key with stripeApiVersion
        EphemeralKeyCreateParams ephemeralKeyParams = EphemeralKeyCreateParams.builder()
                .setCustomer(customer.getId())
                .setStripeVersion(stripeApiVersion)   // ✅ use your env variable
                .build();

        EphemeralKey ephemeralKey = EphemeralKey.create(
                ephemeralKeyParams,
                RequestOptions.builder().build()
        );

        // 3. Create payment intent
        PaymentIntentCreateParams paymentIntentParams = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())
                .setCurrency(request.getCurrency())
                .setCustomer(customer.getId())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentParams);

        // 4. Return response for frontend
        return new PaymentSheetResponse(
                paymentIntent.getClientSecret(),
                ephemeralKey.getSecret(),
                customer.getId(),
                stripePublishableKey
        );
    }
}
