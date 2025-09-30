/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.*;

public class PrescriptionInteractionValidator {

    private static final Map<String, Set<String>> DRUG_INTERACTIONS
            = Map.of(
                    "opioid", Set.of("benzodiazepine", "alcohol"),
                    "benzodiazepine", Set.of("opioid", "alcohol"));

    public static boolean hasInteraction(List<String> medications) {
        for (String drug : medications) {
            Set<String> interactions
                    = DRUG_INTERACTIONS.getOrDefault(drug.toLowerCase(), Collections.emptySet());
            if (!Collections.disjoint(medications, interactions)) {
                return true; // Dangerous interaction found
            }
        }
        return false;
    }

    public static void main(String[] args) {
        List<String> medications = List.of("opioid", "benzodiazepine");
        System.out.println("Interaction detected: " + hasInteraction(medications)); // true
    }
}
