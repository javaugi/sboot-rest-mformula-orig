/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
Common Patterns to Notice:
    State Transition:   Like your assembly line problem, most DP solutions involve choosing between states (e.g., rob/skip, line A/B)
    Space Optimization: Many can be reduced from O(n²) to O(n) or O(1) by tracking only necessary previous states
    Initialization:     Proper setup of base cases is crucial (e.g., dp[0] in coin change)
    Directionality:     Some problems work better forward (coin change), others backward (decode ways)
*/

/*
Key GM-Specific Considerations:
    Real-Time Constraints: All solutions are O(n log n) or better for production-line speed.
    Resource Efficiency: Space optimizations reflect limited memory in factory control systems.
    Industry Standards: Follows ISA-95 for manufacturing execution systems (MES) integration.
*/
public class CommonAlgoPatterns {
    
    
    public char findFirstNonRepeatedCharInString(String s) {
        return s.chars().mapToObj(c -> (char)c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }
    
}
