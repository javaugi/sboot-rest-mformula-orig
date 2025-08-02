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
public class UserActionRecordManager {
    private static final long DOUBLE_CLICK_THRESHOLD_MS = 300; // 300ms window
    private UserActionRecord lastAction;
    
    public static void main(String[] args) {
        UserActionRecordManager manager = new UserActionRecordManager();
        
        Optional<UserActionRecord> action1 = manager.registerClick("Submit");
        Optional<UserActionRecord> action2 = manager.registerClick("Submit");

        action1.ifPresent(a -> System.out.println("Processed action at " + a));
        action2.ifPresent(a -> System.out.println("Processed action at " + a));        
    }

    public Optional<UserActionRecord> registerClick(String actionType) {
        long now = System.currentTimeMillis();
        UserActionRecord newAction = new UserActionRecord(actionType, now);
        //UserActionRecord.builder().actionType(actionType).timestamp(now);

        if (lastAction != null && 
            lastAction.actionType().equals(actionType) &&
            (now - lastAction.timestamp() <= DOUBLE_CLICK_THRESHOLD_MS)) {
            // Ignore or merge this click
            return Optional.empty(); // Treat as same as last one
        }

        lastAction = newAction;
        return Optional.of(newAction); // Treat as new logical action
    }    
}
