/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.billingpayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final StripePaymentService stripePaymentService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // For parsing event data

    public StripeWebhookController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            // Validate the webhook signature
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            System.err.println("Webhook signature verification failed: " + e.getMessage());
            return new ResponseEntity<>("Invalid signature", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error parsing webhook payload: " + e.getMessage());
            return new ResponseEntity<>("Error parsing payload", HttpStatus.BAD_REQUEST);
        }

        // Handle the event
        String eventType = event.getType();
        System.out.println("Received Stripe webhook event: " + eventType);

        switch (eventType) {
            case "payment_intent.succeeded":
                PaymentIntent piSucceeded = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (piSucceeded != null) {
                    String orderId = piSucceeded.getMetadata() != null ? piSucceeded.getMetadata().get("order_id") : null;
                    if (orderId != null) {
                        stripePaymentService.updateOrderStatus(orderId, PaymentStatus.SUCCEEDED);
                    }
                    System.out.println("PaymentIntent succeeded: " + piSucceeded.getId());
                    // Fulfill the order, ship product, send confirmation email, etc.
                }
                break;
            case "payment_intent.payment_failed":
                PaymentIntent piFailed = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (piFailed != null) {
                    String orderId = piFailed.getMetadata() != null ? piFailed.getMetadata().get("order_id") : null;
                    if (orderId != null) {
                        stripePaymentService.updateOrderStatus(orderId, PaymentStatus.FAILED);
                    }
                    System.out.println("PaymentIntent failed: " + piFailed.getId() + " - " + piFailed.getLastPaymentError().getMessage());
                    // Notify customer, mark order as failed, initiate retry process if applicable
                }
                break;
            case "payment_intent.canceled":
                PaymentIntent piCanceled = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (piCanceled != null) {
                    String orderId = piCanceled.getMetadata() != null ? piCanceled.getMetadata().get("order_id") : null;
                    if (orderId != null) {
                        stripePaymentService.updateOrderStatus(orderId, PaymentStatus.CANCELED);
                    }
                    System.out.println("PaymentIntent canceled: " + piCanceled.getId());
                    // Handle cancellation
                }
                break;
            // Add more cases for other relevant events (e.g., 'charge.refunded', 'charge.dispute.created')
            default:
                System.out.println("Unhandled event type: " + eventType);
                break;
        }

        return new ResponseEntity<>("Webhook received and processed", HttpStatus.OK);
    }
}
