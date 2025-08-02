/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.gmprep;

//Problem: Maximize robbery amount without alerting police by robbing adjacent houses.

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HouseRobberByMaxRobberyAmount {
    
    private static final HouseRobberByMaxRobberyAmount main = new HouseRobberByMaxRobberyAmount();
    public static void main(String[] args) {
        int[] nums = {2,4,5,6,7,2};
        log.info("rob  {}", main.rob(nums));
    }
    
    
    /*
    Problem: Maximize robbery amount without alerting police by robbing adjacent houses.
        Key Insight:
            Similar to assembly line problem with two states (rob/skip)
            Maintains only previous two states for optimization
            Time: O(n), Space: O(1)    
    */
    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int prev = 0, curr = 0;
        for (int num : nums) {
            int temp = curr;
            curr = Math.max(prev + num, curr);
            prev = temp;
        }
        return curr;
    }    
}
