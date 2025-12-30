/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.main;

import com.spring5.main.immutable.ImmutableUser;

public class TestBuilderPattern {

    public static void main(String[] args) {
        // Using the builder to create an immutable object with chained method calls
        ImmutableUser user1 = new ImmutableUser.Builder("Lokesh", "Gupta")
                .age(30)
                .phone("1234567")
                .address("Fake address 1234")
                .build();

        System.out.println(user1);

        // Creating another object with only required fields
        ImmutableUser user2 = new ImmutableUser.Builder("Jack", "Reacher")
                .build();

        System.out.println(user2);

        // Attempting to set 'name' to null at build time will throw a NullPointerException
        try {
            ImmutableUser userNoAge = new ImmutableUser.Builder("Jack", "Reacher")
                    .build();
            System.out.println(userNoAge);
        } catch (NullPointerException e) {
            System.out.println("\nSuccessfully caught expected error: " + e.getMessage());
        }
    }
}
