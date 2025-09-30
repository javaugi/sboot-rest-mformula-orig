/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service;

import com.spring5.dbisolation.wmart.Order;
import com.spring5.entity.shoppingcart.CartItem;
import com.spring5.entity.shoppingcart.CustomerInfo;
import com.spring5.entity.shoppingcart.PaymentInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    @Autowired
    private CartService cartService;

    public Order processCheckout(CustomerInfo customerInfo, PaymentInfo paymentInfo) {
        List<CartItem> cartItems = cartService.getCart();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }

        // Validate payment info (in real app, integrate with payment gateway)
        validatePaymentInfo(paymentInfo);

        // Calculate order totals
        double subtotal = cartService.calculateSubtotal();
        double tax = subtotal * 0.08; // 8% tax
        double shipping = 5.99; // Fixed shipping cost
        double total = subtotal + tax + shipping;

        // Create order
        Order order = new Order();
        order.setOrderId(System.currentTimeMillis());
        order.setItems(cartItems);
        order.setCustomerInfo(customerInfo);

        // Mask payment info for security
        PaymentInfo maskedPaymentInfo = new PaymentInfo();
        maskedPaymentInfo.setCardNumber(maskCardNumber(paymentInfo.getCardNumber()));
        maskedPaymentInfo.setExpiryDate(paymentInfo.getExpiryDate());
        maskedPaymentInfo.setCvv("***");
        order.setPaymentInfo(maskedPaymentInfo);

        order.setSubtotal(String.format("%.2f", subtotal));
        order.setTax(String.format("%.2f", tax));
        order.setShipping(String.format("%.2f", shipping));
        order.setTotal(String.format("%.2f", total));
        order.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        // Clear cart after successful checkout
        cartService.clearCart();

        return order;
    }

    private void validatePaymentInfo(PaymentInfo paymentInfo) {
        if (paymentInfo.getCardNumber() == null || paymentInfo.getCardNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Card number is required");
        }
        if (paymentInfo.getExpiryDate() == null || paymentInfo.getExpiryDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Expiry date is required");
        }
        if (paymentInfo.getCvv() == null || paymentInfo.getCvv().trim().isEmpty()) {
            throw new IllegalArgumentException("CVV is required");
        }

        // Basic card number validation (in real app, use proper validation)
        String cleanCardNumber = paymentInfo.getCardNumber().replaceAll("\\s+", "");
        if (cleanCardNumber.length() < 13 || cleanCardNumber.length() > 19) {
            throw new IllegalArgumentException("Invalid card number");
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanCardNumber = cardNumber.replaceAll("\\s+", "");
        return "****" + cleanCardNumber.substring(cleanCardNumber.length() - 4);
    }
}
