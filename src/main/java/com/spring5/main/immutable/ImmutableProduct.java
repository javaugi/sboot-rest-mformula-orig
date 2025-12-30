/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.main.immutable;

import lombok.Builder;
import lombok.NonNull;

/*
To combine the Lombok builder pattern with final fields, the best approach is to use the @Value annotation along with @Builder. The 
    @Value annotation automatically makes all non-static fields private final and generates an all-args constructor, getters, equals, 
    hashCode, and toString methods, ensuring the object is immutable. 

Key Points
Immutability: The combination of @Value and final fields ensures that once an ImmutableProduct object is created using the build() method, 
    its state cannot be modified.
Builder Pattern: The @Builder annotation generates all the necessary boilerplate code for a fluent builder, making object construction easy
    and readable, especially for classes with many fields.
Required Fields: Using the @NonNull annotation enforces that a value must be provided for a specific field during the building process, 
    otherwise a NullPointerException is thrown upon calling build().
Default Values: The @Builder.Default annotation allows you to define a default value for a field if it is not explicitly set in the builder call. 
 */
// @Value ensures all fields are private and final, and generates an all-args constructor and getters.
// @Builder generates the static builder() method and the inner builder class.
@lombok.Value
@Builder
public class ImmutableProduct {

    // Fields are implicitly final due to @Value
    @NonNull
    private String name;
    private final double price; // Explicit final is also fine
    private int quantity;

    // You can use @Builder.Default to provide a default value for a final field
    @Builder.Default
    private final boolean inStock = true;

    // Lombok will generate a constructor like this internally:
    // ImmutableProduct(String name, double price, int quantity, boolean inStock) {
    //     this.name = name;
    //     this.price = price;
    //     this.quantity = quantity;
    //     this.inStock = inStock;
    // }
    // Lombok also generates public getters (e.g., getName(), getPrice(), etc.)
    public static void main(String[] args) {
        // Usage of the generated builder:
        ImmutableProduct product = ImmutableProduct.builder()
                .name("Laptop")
                .price(999.99)
                .quantity(50)
                // inStock is true by default
                .build();

        // Accessing the final fields via getters
        System.out.println("Product Name: " + product.getName());
        System.out.println("Price: $" + product.getPrice());
        System.out.println("Quantity: " + product.getQuantity());
        System.out.println("In Stock: " + product.isInStock());

        // Attempting to set 'name' to null at build time will throw a NullPointerException
        try {
            ImmutableProduct invalidProduct = ImmutableProduct.builder()
                    .price(100.00)
                    .quantity(10)
                    .build();
        } catch (NullPointerException e) {
            System.out.println("\nSuccessfully caught expected error: " + e.getMessage());
        }
    }
}
