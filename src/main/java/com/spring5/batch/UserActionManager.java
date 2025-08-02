/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.batch;

import java.util.Optional;

/**
 *
 * @author javaugi
 */
public class UserActionManager {
    private static final long DOUBLE_CLICK_THRESHOLD_MS = 300; // 300ms window
    private UserAction lastAction;
    
    public static void main(String[] args) {
        UserActionManager manager = new UserActionManager();

        Optional<UserAction> action1 = manager.registerClick("Submit");
        Optional<UserAction> action2 = manager.registerClick("Submit");

        action1.ifPresent(a -> System.out.println("Processed action at " + a));
        action2.ifPresent(a -> System.out.println("Processed action at " + a));        
    }

    public Optional<UserAction> registerClick(String actionType) {
        long now = System.currentTimeMillis();
        UserAction newAction = new UserAction(actionType, now);

        if (lastAction != null && 
            lastAction.getActionType().equals(actionType) &&
            (now - lastAction.getTimestamp() <= DOUBLE_CLICK_THRESHOLD_MS)) {
            // Ignore or merge this click
            return Optional.empty(); // Treat as same as last one
        }

        lastAction = newAction;
        return Optional.of(newAction); // Treat as new logical action
    }    
}
