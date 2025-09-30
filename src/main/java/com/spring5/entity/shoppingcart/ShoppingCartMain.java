/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.shoppingcart;

/**
 * @author javau
 */
public class ShoppingCartMain {
}

/*
src/main/java/com/shop/app/
├── controller/
│   ├── ProductController.java
│   ├── CartController.java
│   └── CheckoutController.java
├── service/
│   ├── ProductService.java
│   ├── CartService.java
│   └── CheckoutService.java
├── model/
│   ├── Product.java
│   ├── ProductVariant.java
│   ├── CartItem.java
│   ├── Cart.java
│   ├── Order.java
│   ├── CustomerInfo.java
│   └── PaymentInfo.java
├── dto/
│   ├── AddToCartRequest.java
│   ├── CheckoutRequest.java
│   └── ApiResponse.java
└── ShoppingApplication.java
 */

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
