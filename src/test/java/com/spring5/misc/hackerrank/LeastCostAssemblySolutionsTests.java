/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc.hackerrank;

import com.interview.gmprep.AssemblyLineParallelStationsWithFixedTransfer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LeastCostAssemblySolutionsTests {

    // @Test
    public void testLongestSubstring_Parameterized() {
        System.out.println("LeastCostAssemblySolutionsTests");
        AssemblyLineParallelStationsWithFixedTransfer main
                = new AssemblyLineParallelStationsWithFixedTransfer();

        int[] A = {2, 8, 4, 12, 15};
        int[] B = {4, 7, 5, 10, 12};
        int X = 8, Y = 4, total = 38;
        // 2 + 4 + 7 + 5 + 10 + 12  = 40
        int totalCost = main.solutionOptimized(A, B, X, Y);
        int totalCost2 = main.solution(A, B, X, Y);
        System.out.println(
                "LeastCostAssemblySolutionsTests cost=" + totalCost + "-cost2=" + totalCost2);
        assertEquals(total, totalCost);
    }
}
