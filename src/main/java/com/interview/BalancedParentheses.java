/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview;

import java.util.*;
import static java.util.Map.entry;
import lombok.extern.slf4j.Slf4j;

/*
Explanation:
    Stack Usage: We use a stack to keep track of opening parentheses.
    Pushing: When we encounter an opening parenthesis ((, {, [), we push it onto the stack.
    Popping: When we encounter a closing parenthesis (), }, ]), we check if it matches the top of the stack:
    If the stack is empty, it's unbalanced.
    If the closing parenthesis doesn't match the top of the stack, it's unbalanced.
    Final Check: After processing all characters, if the stack is empty, the string is balanced; otherwise, it's not.

Key Points:
    Time Complexity: O(n) where n is the length of the string (each character is processed once).
    Space Complexity: O(n) in the worst case (when all characters are opening parentheses).
    Edge Cases: Handles empty strings (returns true) and strings with only closing parentheses (returns false).
    This solution efficiently checks for balanced parentheses using the LIFO principle of stacks.
 */
@Slf4j
public class BalancedParentheses {

    public static void main(String[] args) {
        /*
    Scanner scanner = new Scanner(System.in);

    while (scanner.hasNext()) {
        String input = scanner.nextLine();
        System.out.println(isBalanced(input));
    }
    // */
        List<String> inputs = Arrays.asList("{}()", "({()})", "{}(", "[])", "b[]a");
        System.out.println("Printing Map ...");
        printMap();

        for (String input : inputs) {
            String value = (isBalanced(input) ? "true" : "false");
            String value2 = (isBalancedImproved(input) ? "true" : "false");
            log.debug("\n input={}     bal 1?={}   bal 2?={}", input, value, value2);
        }

        if (true) {
            return;
        }

        System.out.println("1 isBalanced ...");
        for (String input : inputs) {
            String value = (isBalanced(input) ? "true" : "false");
            String value2 = (isBalanced2(input) ? "true" : "false");
            String value3 = (isBalancedParentheses(input) ? "true" : "false");
            String value4 = (isValidParentheses(input) ? "true" : "false");
            String value5 = (isValidParentheses2(input) ? "true" : "false");
            log.debug(
                    "\n input={}     bal 1?={}   bal 2?={}   bal 3?={} bal 4?={}   bal 5?={}",
                    input,
                    value,
                    value2,
                    value3,
                    value4,
                    value5);
        }

        if (true) {
            return;
        }

        System.out.println("2 isBalanced ...");
        for (String input : inputs) {
            String value = (isBalanced2(input) ? "true" : "false");
            System.out.println(value);
        }
        System.out.println("3 isBalanced ...");
        for (String input : inputs) {
            String value = (isValidParentheses(input) ? "true" : "false");
            System.out.println(value);
        }
        System.out.println("4 isBalanced ...");
        for (String input : inputs) {
            String value = (isValidParentheses2(input) ? "true" : "false");
            System.out.println(value);
        }
    }

    static Map<Character, Character> map
            = Map.of(
                    '}', '{',
                    ']', '[',
                    ')', '(');

    private static void printMap() {
        map.keySet().stream()
                .forEach(
                        k -> {
                            System.out.println("printMap key=" + k + "-value=" + map.get(k));
                        });
    }

    public static boolean isBalancedImproved(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
            } else if (stack.isEmpty() || map.get(c) == null || stack.pop() != map.get(c)) {
                // the stack should not be empty since there must be {, [, ( in it already if it is balanced
                return false;
            }
        }

        return stack.isEmpty();
    }

    public static boolean isBalanced(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }

                char top = stack.pop();
                if ((c == '}' && top != '{') || (c == ']' && top != '[') || (c == ')' && top != '(')) {
                    return false;
                }

                /*
        if (!((c == '}' && top == '{') ||
              (c == ']' && top == '[') ||
              (c == ')' && top == '('))) {
            return false;
        }
        // */
            }
        }

        return stack.isEmpty();
    }

    public static boolean isBalancedParentheses(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
            } else {
                // the stack should not be empty since there must be {, [, ( in it already if it is balanced
                if (stack.isEmpty()) {
                    return false;
                }

                char top = stack.pop(); // top should be {, [, (
                // log.debug("c=" + c + "-top=" + top + "-map.get(c) expectedOpening =" + map.get(c));
                if (map.get(c) != null) {
                    char expectedOpening = map.get(c);
                    // log.debug("top {} pair {}", top, expectedOpening);
                    if (top != expectedOpening) {
                        return false;
                    }
                }
            }
        }

        return stack.isEmpty();
    }

    public static boolean isBalanced2(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }

                char top = stack.pop();
                if (charOutOfBalance2(c, top)) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    private static boolean charOutOfBalance(char c, char top) {
        return !((c == '}' && top == '{') || (c == ']' && top == '[') || (c == ')' && top == '('));
    }

    private static boolean charOutOfBalance2(char c, char top) {
        return (c == '}' && top != '{') || (c == ']' && top != '[') || (c == ')' && top != '(');
    }

    public static boolean isValidParentheses(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(')');
            } else if (c == '{') {
                stack.push('}');
            } else if (c == '[') {
                stack.push(']');
            } else if (stack.isEmpty() || stack.pop() != c) {
                return false;
            }
        }
        return stack.isEmpty();
    }

    Map<String, Integer> studentScores
            = Map.ofEntries(
                    entry("Alice", 95),
                    entry("Bob", 88),
                    entry("Charlie", 92),
                    entry("Diana", 78),
                    entry("Eve", 100));

    public static boolean isValidParentheses2(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }

                char top = stack.pop();
                // log.debug("c=" + c + "-top=" + top + "-map.get(c) cPair=" + map.get(c));
                if (map.get(c) != null) {
                    char expectedOpening = map.get(c);
                    // log.debug("top {} pair {}", top, expectedOpening);
                    if (top != expectedOpening) {
                        return false;
                    }
                }
            }

            /*
      if (c == '(') {
          stack.push(')');
      } else if (c == '{') {
          stack.push('}');
      } else if (c == '[') {
          stack.push(']');
      } else if (stack.isEmpty() || stack.pop() != c) {
          return false;
      }
      // */
        }
        return stack.isEmpty();
    }
}

/*

please write me a program for this scenario below In computer science, a stack or LIFO (last in, first out) is an abstract data type that serves as a collection of elements, with two principal operations: push, which adds an element to the collection, and pop, which removes the last element that was added.(Wikipedia)
A string containing only parentheses is balanced if the following is true: 1. if it is an empty string 2. if A and B are correct, AB is correct, 3. if A is correct, (A) and {A} and [A] are also correct.

Examples of some correctly balanced strings are: "{}()", "[{()}]", "({()})"

Examples of some unbalanced strings are: "{}(", "({)}", "[[", "}{" etc.

Given a string, determine if it is balanced or not.

Input Format

There will be multiple lines in the input file, each having a single non-empty string. You should read input till end-of-file.

The part of the code that handles input operation is already provided in the editor.

Output Format

For each case, print 'true' if the string is balanced, 'false' otherwise.

Sample Input

{}()
({()})
{}(
[]
Sample Output

true
true
false
true
 */
