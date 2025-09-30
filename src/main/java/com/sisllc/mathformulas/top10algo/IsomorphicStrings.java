/*
 * Copyright (C) 2018 Strategic Information Systems, LLC.
 *
 */
package com.sisllc.mathformulas.top10algo;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author javaugi
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public class IsomorphicStrings {

    private static final Logger log = LoggerFactory.getLogger(IsomorphicStrings.class);

    /*
  To check if two strings are isomorphic, it means determining if a one-to-one mapping exists between the characters of the two strings.
      In other words, each character in the first string must map to a unique character in the second string, and vice-versa, while
      maintaining the order of characters.

  Key points:
      Same Length: Isomorphic strings must have the same length. If the lengths differ, they cannot be isomorphic.
      One-to-One Mapping: Each character in the first string must map to a unique character in the second string. No two characters
          in the first string can map to the same character in the second string.
      Order Preservation: The order of characters must be maintained during the mapping.
      Self-Mapping: A character can map to itself.
  Example:
      "egg" and "add" are isomorphic because 'e' can map to 'a', and 'g' can map to 'd'.
      "foo" and "bar" are not isomorphic because 'o' in "foo" would need to map to both 'a' and 'r' in "bar," violating the one-to-one mapping rule.
      "paper" and "title" are isomorphic because 'p' maps to 't', 'a' maps to 'i', 'e' maps to 'l', and 'r' maps to 'e'.
  In Summary: Isomorphic strings must have the same length, but having the same length is not enough to conclude that two strings are isomorphic.
      The key is the existence of a one-to-one mapping between the characters of the two strings while preserving the order.
     */
    public static void main(String[] args) {
        System.out.println("egg vs add: " + isoMorphic("egg", "add"));
        System.out.println("aabbb vs ddeee: " + isoMorphic("aabbb", "ddeee"));
        System.out.println("abcde vs fghij: " + isoMorphic("abcde", "fghij"));
        System.out.println("abcde vs efghi: " + isoMorphic("abcde", "efghi"));
        System.out.println("foo vs bar: " + isoMorphic("foo", "bar"));
    }

    public static boolean isoMorphic(String s, String t) {
        if (s == null || t == null) {
            return false;
        }
        if (s.length() != t.length()) {
            return false;
        }

        HashMap<Character, Character> map = new HashMap<Character, Character>();
        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = t.charAt(i);

            if (map.containsKey(c1)) {
                // if not consistant with previous ones
                if (map.get(c1) != c2) {
                    return false;
                }
            } else {
                // if c2 is already being mapped. Time complexity O(n) here
                if (map.containsValue(c2)) {
                    return false;
                }
                map.put(c1, c2);
            }
        }

        return true;
    }
}
