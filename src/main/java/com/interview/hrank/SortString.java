/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SortString {

    private static final SortString ss = new SortString();

    public static void main(String[] args) {
        String sToSort = "idfhghg";
        ss.doSorting(sToSort);
    }

    private void doSorting(String s) {
        log.debug("1 doSorting {}", s);
        char[] sArr = s.toCharArray();
        log.debug("2 Before {}", Arrays.toString(sArr));
        Arrays.sort(sArr);
        log.debug("3 Before {}", Arrays.toString(sArr));
        String newStr = new String(sArr);
        log.debug("4 sorted {}", newStr);
    }
}
