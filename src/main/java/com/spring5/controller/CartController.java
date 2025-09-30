/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.dto.AddToCartRequest;
import com.spring5.entity.shoppingcart.CartItem;
import com.spring5.service.CartService;
import com.spring5.utils.pact.ApiResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ApiResponse<List<CartItem>> getCart() {
        List<CartItem> cart = cartService.getCart();
        return ApiResponse.success(cart);
    }

    @PostMapping("/add")
    public ApiResponse<CartItem> addToCart(@RequestBody AddToCartRequest request) {
        try {
            CartItem cartItem
                    = cartService.addToCart(
                            request.getProductId(), request.getVariant(), request.getQuantity());
            return ApiResponse.success("Item added to cart", cartItem);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/update/{itemId}")
    public ApiResponse<CartItem> updateQuantity(
            @PathVariable String itemId, @RequestBody UpdateQuantityRequest request) {
        Optional<CartItem> updatedItem = cartService.updateQuantity(itemId, request.getQuantity());
        if (updatedItem.isPresent()) {
            return ApiResponse.success("Quantity updated", updatedItem.get());
        } else {
            return ApiResponse.error("Item not found in cart");
        }
    }

    @DeleteMapping("/remove/{itemId}")
    public ApiResponse<String> removeFromCart(@PathVariable String itemId) {
        boolean removed = cartService.removeFromCart(itemId);
        if (removed) {
            return ApiResponse.success("Item removed from cart", null);
        } else {
            return ApiResponse.error("Item not found in cart");
        }
    }

    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart() {
        cartService.clearCart();
        return ApiResponse.success("Cart cleared", null);
    }

    // Helper class for update quantity request
    public static class UpdateQuantityRequest {

        private int quantity;

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}

/*
create Online Shopping website which has 3 products for sale:
1) coffe mugs - 3 sizes (8oz, 12oz, 16oz) and 4 colors (Red, Green, white, black)
2) T-Shirts - 4 types Men, Women, Boys, Girls and 3 sizes each (S, M, L) and 3 colors (Pink, Orange, Blue)
3) soccerballs - 3 colors (green, orange, purple)

Create backend application to Display these products and allow users to buy single or multiple products of same or different category, of same or
    different type, allowing all possible combinations of products to be purchased by using single checkout experience.
 */
 /*
problem: Use spring initializr to create a spring boot project and create an endpoint that accepts multiple payments objects in a single request.
Request structure could be as below -
{
    "requestNumber": "batch-number-1", // should be unique for every request, payments are sent in a batch
    "payments": [
        {
            "paymentId": "pid-123", // must be unique for every request
            "amountToBePaid": 1000,
            "employeeId": "ABC-123" // who is getting paid
        },
        {
            "paymentId": "pid-345", // must be unique for every request
            "amountToBePaid": 500,
            "employeeId": "XYZ-789"  // who is getting paid
        }
    ]
}
Define response as per your understanding.
reqeust data can be saved to hashmap if required.
Tasks to be completed:
1) Create endpoint a REST endpoint with proper validations for the request object.
2) Payments execution requirements: create a PaymentsService that will process these payments in a sequence one by one.
 */
