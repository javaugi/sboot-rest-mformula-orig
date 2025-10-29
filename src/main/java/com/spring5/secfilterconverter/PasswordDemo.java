/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

// Usage
public class PasswordDemo {

    public static void main(String[] args) throws Exception {
        PasswordService service = new PasswordService();

        String password = "mySecretPassword123";
        String hashedPassword = service.hashPassword(password);

        System.out.println("Hashed password: " + hashedPassword);
        System.out.println("Verification: "
                + service.verifyPassword(password, hashedPassword)); // true
        System.out.println("Wrong password: "
                + service.verifyPassword("wrong", hashedPassword)); // false
    }
}
