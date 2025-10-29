/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.func.combousage;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
Key Takeaways:
    Consumer: Accepts input, returns void - used in forEach()
    Predicate: Accepts input, returns boolean - used in filter()
    Function: Accepts input, returns output - used in map()
    Supplier: No input, returns output - used in generate()
These functional interfaces form the foundation of Java's functional programming capabilities and are extensively used with Stream API for concise, readable data processing code.
 */
public class CombinedFunctionalInterfaces {

    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("John", "IT", 75000, 28),
                new Employee("Jane", "HR", 65000, 32),
                new Employee("Jack", "IT", 80000, 35),
                new Employee("Alice", "Finance", 70000, 29),
                new Employee("Bob", "IT", 90000, 42)
        );

        // Predicate: Filter IT department employees
        Predicate<Employee> itDepartment = e -> "IT".equals(e.getDepartment());
        Predicate<Employee> highSalary = e -> e.getSalary() > 70000;
        Predicate<Employee> experienced = e -> e.getAge() > 30;

        // Function: Transform employee to string
        Function<Employee, String> employeeToString
                = e -> e.getName() + " - " + e.getSalary();

        // Function: Calculate bonus
        Function<Employee, Double> bonusCalculator
                = e -> e.getSalary() * 0.1;

        // Consumer: Print employee details
        Consumer<Employee> printEmployee
                = e -> System.out.println(e.getName() + " (" + e.getDepartment() + "): $" + e.getSalary());

        System.out.println("=== IT Department Employees ===");
        employees.stream()
                .filter(itDepartment)
                .forEach(printEmployee);

        System.out.println("\n=== Experienced IT Employees with High Salary ===");
        List<String> experiencedItEmployees = employees.stream()
                .filter(itDepartment.and(highSalary).and(experienced))
                .map(employeeToString)
                .collect(Collectors.toList());
        experiencedItEmployees.forEach(System.out::println);

        System.out.println("\n=== Employees with Bonus ===");
        employees.stream()
                .filter(highSalary)
                .map(e -> {
                    double bonus = bonusCalculator.apply(e);
                    return e.getName() + ": Salary $" + e.getSalary() + ", Bonus $" + bonus;
                })
                .forEach(System.out::println);

        // Supplier for summary statistics
        Supplier<String> summarySupplier = () -> {
            DoubleSummaryStatistics stats = employees.stream()
                    .mapToDouble(Employee::getSalary)
                    .summaryStatistics();
            return String.format("Salary Stats: Avg=%.2f, Max=%.2f, Min=%.2f",
                    stats.getAverage(), stats.getMax(), stats.getMin());
        };

        System.out.println("\n summarySupplier:" + summarySupplier.get());

        // Supplier for summary statistics
        Supplier<String> summaryAgeSupplier = () -> {
            IntSummaryStatistics stats = employees.stream()
                    .mapToInt(Employee::getAge)
                    .summaryStatistics();
            return String.format("Age Stats: Avg=%.2f, Max=%d, Min=%d%n",
                    stats.getAverage(), stats.getMax(), stats.getMin());
        };

        System.out.println("\n summaryAgeSupplier: " + summaryAgeSupplier.get());
        int number = 123;
        System.out.printf("The number is: %d%nThis is a new line.%n", number);
        //%n automatically resolves to \n on Unix-like systems (Linux, macOS) and \r\n (carriage return followed by line feed) on Windows systems. 

        // Grouping and mapping
        System.out.println("\n=== Employees by Department ===");
        Map<String, List<String>> employeesByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.mapping(Employee::getName, Collectors.toList())
                ));
        employeesByDept.forEach((dept, names)
                -> System.out.println(dept + ": " + names));

    }

    static class Employee {

        private String name;
        private String department;
        private double salary;
        private int age;

        public Employee(String name, String department, double salary, int age) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.age = age;
        }

        // Getters
        public String getName() {
            return name;
        }

        public String getDepartment() {
            return department;
        }

        public double getSalary() {
            return salary;
        }

        public int getAge() {
            return age;
        }
    }
}
