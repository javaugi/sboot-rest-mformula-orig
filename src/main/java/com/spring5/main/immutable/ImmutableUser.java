/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.main.immutable;

public final class ImmutableUser {

    // 1. Make all fields private and final
    private final String firstName;
    private final String lastName;

    private final int age;
    private final String phone;
    private final String address;

    // 2. Private constructor that takes the Builder object as an argument
    private ImmutableUser(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    // 3. Provide public getter methods (no setters)
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "ImmutableUser{"
                + "firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", age=" + age
                + ", phone='" + phone + '\''
                + ", address='" + address + '\''
                + '}';
    }

    // 4. Static nested Builder class
    public static class Builder {

        // Required parameters are final or in the builder's constructor
        private final String firstName;
        private final String lastName;

        // Optional parameters are non-final in the builder
        private int age;
        private String phone;
        private String address;

        // Public constructor for required fields
        public Builder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        // Setter methods for optional parameters return the Builder object (for chaining)
        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        // Final step: a build method to create the ImmutableUser instance
        public ImmutableUser build() {
            // Optional: Perform validation before creating the final object
            if (age < 0) {
                throw new IllegalArgumentException("Age cannot be negative");
            }
            return new ImmutableUser(this);
        }
    }
}
