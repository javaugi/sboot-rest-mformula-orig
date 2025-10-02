/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank;

import com.spring5.empbilpayroll.Employee;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author javau
 */
public class TransformerMain {

	/*
	 * Summary Default/Static Methods: Evolve interfaces while maintaining backward
	 * compatibility. Functional Interfaces: Enable functional programming (lambdas) in
	 * Java. They complement each other (e.g., many java.util.function interfaces use
	 * default methods).
	 * 
	 * Why This Matters Backward Compatibility: Default methods let Java evolve interfaces
	 * (e.g., List.sort()) without breaking existing code. Functional Programming:
	 * Functional interfaces (Comparator, Consumer) enable clean lambda-based APIs.
	 * Readability: Static methods like Comparator.comparing() provide intuitive factory
	 * methods.
	 * 
	 * This combination is widely used in: Java Streams API (e.g.,
	 * Stream.filter(Predicate)). Java Time API (e.g., LocalDate.now() static method).
	 * Collections framework (as shown above).
	 */
	private static TransformerMain m = new TransformerMain();

	public static void main(String[] args) {
		// Usage:
		Transformer<String> upperCase = String::toUpperCase;
		Transformer<String> exclaim = s -> s + "!";
		Transformer<String> pipeline = upperCase.andThen(exclaim);

		System.out.println(pipeline.transform("hello")); // Output: HELLO!
		m.run();
	}

	private void run() {
		List<Employee> employees = Arrays.asList(
				Employee.builder().firstName("Alice").lastName("Smith").salary(75000f).build(),
				Employee.builder().firstName("Amy").lastName("Swift").salary(65000f).build(),
				Employee.builder().firstName("John").lastName("Doe").salary(95000f).build(),
				Employee.builder().firstName("Dog").lastName("Kerry").salary(105000f).build());

		// Using Comparator's static method and default method chaining
		Comparator<Employee> byName = Comparator.comparing(Employee::getLastName);
		Comparator<Employee> bySalary = Comparator.comparingDouble(Employee::getSalary);

		// Default method: sort() in List interface
		employees.sort(byName.thenComparing(bySalary)); // Chained comparators

		employees.forEach(e -> System.out.println(e.getLastName() + ": " + e.getSalary()));
	}

	/*
	 * Key Takeaways Feature Example in Collections API Benefit Default Method List.sort()
	 * All List implementations get sorting without code duplication. Static Method
	 * Comparator.comparing() Factory method to easily create comparators (e.g., by
	 * field). Functional Interface Comparator Enables lambdas (e.g., Employee::getName).
	 * Default Method Chaining thenComparing() Combines multiple comparators (e.g., sort
	 * by name, then by salary). How They Work Together List.sort() (default method)
	 * accepts a Comparator (functional interface). Comparator.comparing() (static method)
	 * creates a Comparator from a lambda. thenComparing() (default method in Comparator)
	 * chains multiple comparators.
	 */

}
