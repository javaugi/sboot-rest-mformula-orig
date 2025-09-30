/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.misc;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StringSwapTests {

    @Test
    public void stringSwap2charsOnly() {
        StringSwapHelper help = new StringSwapHelper();
        Assert.assertEquals("BA", help.swaplasttwochars("AB"));
        Assert.assertEquals("ABCD", help.swaplasttwochars("DCBA"));
    }

    private static class StringSwapHelper {

        public StringSwapHelper() {
        }

        private String swaplasttwochars(String ab) {
            StringBuilder rv = new StringBuilder();
            for (int i = ab.length() - 1; i >= 0; i--) {
                rv.append(ab.charAt(i));
            }

            return rv.toString();
        }
    }
}
