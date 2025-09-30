/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date; // Date is a mutable class
import java.util.List;

public final class ImmutablePatientRecord {

    private final String patientId;
    private final String name;
    private final Date dateOfBirth; // Mutable field
    private final List<String> allergies; // Mutable collection

    public ImmutablePatientRecord(
            String patientId, String name, Date dateOfBirth, List<String> allergies) {
        // 1. Assign final fields
        this.patientId = patientId;
        this.name = name;

        // 2. Defensive copy for mutable Date object
        this.dateOfBirth = new Date(dateOfBirth.getTime()); // Create a new Date object

        // 3. Defensive copy for mutable List
        this.allergies = new ArrayList<>(allergies); // Create a new ArrayList
    }

    // No setter methods
    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public Date getDateOfBirth() {
        // 4. Return defensive copy of mutable Date
        return new Date(dateOfBirth.getTime());
    }

    public List<String> getAllergies() {
        // 5. Return immutable view of the list or a defensive copy
        // Collections.unmodifiableList() is often preferred for performance if the list is large.
        // If the list itself needs to be copied when returned, then new ArrayList<>(this.allergies) is
        // needed.
        return Collections.unmodifiableList(allergies);
    }

    @Override
    public String toString() {
        return "ImmutablePatientRecord{"
                + "patientId='"
                + patientId
                + '\''
                + ", name='"
                + name
                + '\''
                + ", dateOfBirth="
                + dateOfBirth
                + ", allergies="
                + allergies
                + '}';
    }

    public static void main(String[] args) {
        Date dob = new Date();
        List<String> patientAllergies = new ArrayList<>();
        patientAllergies.add("Peanuts");

        ImmutablePatientRecord record
                = new ImmutablePatientRecord("P123", "Alice Smith", dob, patientAllergies);
        System.out.println("Original Record: " + record);

        // Try to modify the 'mutable' fields passed in or returned
        dob.setTime(0); // Modify original Date object
        patientAllergies.add("Dust"); // Modify original List object

        System.out.println("After attempting to modify inputs: " + record); // Record remains unchanged!

        Date retrievedDob = record.getDateOfBirth();
        retrievedDob.setTime(1000); // Modify the returned Date object

        List<String> retrievedAllergies = record.getAllergies();
        try {
            retrievedAllergies.add("Pollen"); // This will throw UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("Attempted to modify returned list: " + e.getMessage());
        }

        System.out.println(
                "After attempting to modify returned objects: " + record); // Record remains unchanged!
    }
}
