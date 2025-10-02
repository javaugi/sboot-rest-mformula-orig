/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.empbilpayroll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GroupEmployeesByDeptAvgSalary {

	public static void main(String[] args) {
		GroupEmployeesByDeptAvgSalary main = new GroupEmployeesByDeptAvgSalary();

		List<Employee> employees = EmployeeClient.setupEmployees();
		log.info("Solution 1 ...");
		main.groupEmpByDeptAvgSalary(employees);

		log.info("Solution 2 ...");
		main.groupEmpByDeptAvgSalary2(employees);

		log.info("\n *** Find the first non-repeated character in a string using Java Streams ...");
		String str = "huhhjkjdggfgfg";
		log.info(" first non-repeated char Found {} from original string {}", firstNonRepeatedChar(str), str);
	}

	private void groupEmpByDeptAvgSalary(List<Employee> employees) {
		Map<String, Double> avgSalaryByDept = employees.stream()
			.collect(Collectors.groupingBy(Employee::getDepartName, Collectors.averagingDouble(Employee::getSalary)));

		avgSalaryByDept.entrySet()
			.iterator()
			.forEachRemaining(
					e -> log.info("2  Department {}, Avg Salary {}", e.getKey(), String.format("%.2f", e.getValue())));
	}

	private void groupEmpByDeptAvgSalary2(List<Employee> employees) {
		Map<String, Double> avgSalaryByDept = employees.stream()
			.collect(Collectors.groupingBy(Employee::getDepartName,
					// Use collectingAndThen to apply rounding after averaging
					Collectors.collectingAndThen(Collectors.averagingDouble(Employee::getSalary), average -> {
						// Use BigDecimal for precise rounding
						BigDecimal bd = new BigDecimal(Double.toString(average));
						bd = bd.setScale(2, RoundingMode.HALF_UP); // Round to 2 decimal
																	// places, HALF_UP is
																	// common
						return bd.doubleValue();
					})));

		// Print the results
		avgSalaryByDept.forEach(
				(department, avgSalary) -> log.info("1 Department: " + department + ", Average Salary: " + avgSalary));
	}

	public static char firstNonRepeatedChar(String s) {
		return s.chars()
			.mapToObj(c -> (char) c)
			.collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
			.entrySet()
			.stream()
			.filter(entry -> entry.getValue() == 1)
			.map(Map.Entry::getKey)
			.findFirst()
			.orElseThrow();
	}

}
