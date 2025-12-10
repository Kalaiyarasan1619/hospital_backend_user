package com.hospital_management.controller;


import com.hospital_management.dto.PaymentSheetRequest;
import com.hospital_management.dto.PaymentSheetResponse;
import com.hospital_management.service.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-payment-sheet")
    public ResponseEntity<?> createPaymentSheet(@RequestBody PaymentSheetRequest request) {
        try {
            // ✅ Input validation
            if (request.getAmount() == null || request.getAmount() <= 0 ||
                request.getCurrency() == null || request.getCurrency().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid amount or currency"));
            }

            // ✅ Create payment sheet response
            PaymentSheetResponse response = stripeService.createPaymentSheetParams(request);

            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Stripe error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}
