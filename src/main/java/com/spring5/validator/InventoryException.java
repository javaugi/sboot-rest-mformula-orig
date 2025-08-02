/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.validator;

/**
 *
 * @author javaugi
 */
public class InventoryException extends RuntimeException {
    public InventoryException(String message) {
        super(message);
    }
    public InventoryException(String message, Exception e) {
        super(message);
    }
}
