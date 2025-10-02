/*
 * Copyright (C) 2019 Strategic Information Systems, LLC.
 *
 */
package com.spring5.empbilpayroll;

import com.spring5.MyApplication;
import com.spring5.service.NativeQueryService;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * @author javaugi
 * @version $LastChangedRevision $LastChangedDate Last Modified Author: $LastChangedBy
 */
@Component
public class EmployeeClient {

	private static final Logger log = LoggerFactory.getLogger(EmployeeClient.class);

	public static final List<String> firstNames = List.of("Abbey", "Adam", "Alan", "Albert", "Alice", "Amy", "Jax",
			"John", "Jonathan", "Jeffrey", "Barbara", "Ben", "Bernie", "Byron", "Jemma", "Carl", "Carol", "Cathy",
			"Dan", "David", "Mark", "Mike", "Kevin", "Will");

	public static final List<String> lastNames = List.of("Smith", "Lee", "Barkley", "Green", "Clinton", "Bloom",
			"Bryant", "Collins", "Gere", "Dewey", "Doe", "Stanford", "Shultz", "Washington", "Wolf");

	public static final List<String> middleInitial = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
			"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

	public static final List<Double> salaries = List.of(83578.15, 55672.55, 84567.45, 102321.65, 102593.33, 843854.33,
			67345.35, 73895.44, 93461.98, 89410.48, 135921.32, 176543.77, 197043.22, 89054.44, 54843.22);

	public static final Map<String, String> deptMap = Map.ofEntries(Map.entry("1111", "Sales"),
			Map.entry("1112", "Marketing"), Map.entry("1113", "IT"), Map.entry("1114", "HR"),
			Map.entry("1115", "Customer Relations"), Map.entry("1116", "Products"), Map.entry("1117", "Researches"),
			Map.entry("1118", "Financial"), Map.entry("1119", "Security"), Map.entry("1120", "Administration"));

	private static final int PAGE_SIZE = 10;

	private static final String QUERY_EM_DEPT_HIGHEST_SAL = "select e.lastName, e.firstName, e.middleInitial, e.salary, e.deptCode "
			+ " from employee e join department d on d.code = e.deptCode "
			+ " where e.salary = (select max(e0.salary) from employee e0 where e0.deptCode = e.deptCode )"
			+ " order by d.code desc";

	private static final String QUERY_EM_DEPT_HIGHEST_SAL2 = "select e.lastName, e.firstName, e.middleInitial, e.salary, e.deptCode "
			+ " from employee e "
			+ " where e.salary = (select max(e0.salary) from employee e0 where e0.deptCode = e.deptCode )"
			+ " order by e.deptCode desc";

	@Autowired
	private EmployeeRepository repo;

	@Autowired
	private DepartmentRepository deptRepo;

	@Autowired
	private NativeQueryService nativeQueryService;

	public static void main(String[] args) {

		EmployeeClient main = new EmployeeClient();
		// Start the Spring application and get the application context
		ConfigurableApplicationContext context = SpringApplication.run(MyApplication.class, args);
		log.info("FirstShortestLongestCityNames started with context {}", context);

		main.init();

		// Close the application context when done
		context.close();
	}

	@PostConstruct
	public void init() {
		log.info("EmployeeClient init");
		try {
			setup();
			run();
		}
		catch (Exception ex) {
			log.error("Error EmployeeClient init ", ex);
		}
	}

	public void run() {
		log.info("EmployeeClient display employees");
		Iterable<Employee> all = repo.findAll();
		all.forEach(System.out::println);

		log.info("EmployeeClient display departments");
		Iterable<Department> allDepts = deptRepo.findAll();
		allDepts.forEach(System.out::println);

		log.info("1 EmployeeClient display highest salary employees for each department \n ########");
		highestSalaryByDepartment(QUERY_EM_DEPT_HIGHEST_SAL);
		log.info("2 EmployeeClient display highest salary employees for each department \n ########");
		highestSalaryByDepartment(QUERY_EM_DEPT_HIGHEST_SAL2);
		log.info(" \n ######## \n Done EmployeeClient display highest salary employees for each department");

		runSliceDisplay();
	}

	private void highestSalaryByDepartment(String qString) {
		List<String> list = nativeQueryService.doQuery(qString);
		list.stream().forEach(System.out::println);
	}

	private void runSliceDisplay() {
		System.out.println(" -- paginating where dept is Sales --");
		Slice<Employee> slice = null;
		Pageable pageable = PageRequest.of(0, 3, Sort.by("salary"));
		while (true) {
			slice = repo.findByDeptName("Sales");
			int number = slice.getNumber();
			int numberOfElements = slice.getNumberOfElements();
			int size = slice.getSize();
			System.out.printf("slice info - page number %s, numberOfElements: %s, size: %s%n", number, numberOfElements,
					size);
			List<Employee> employeeList = slice.getContent();
			employeeList.forEach(System.out::println);
			if (!slice.hasNext()) {
				break;
			}
			pageable = slice.nextPageable();
		}
	}

	private final int n = 6;

	private void setup() {
		List<Employee> employees = new ArrayList();
		List<Department> depts = new ArrayList<>();
		Employee emp;
		Department dept;

		Random rand = new Random();

		Set<String> keySet = deptMap.keySet();
		for (String code : keySet) {
			dept = create(code, deptMap.get(code));
			depts.add(dept);

			int i = 0;
			while (i++ < n) {
				emp = create(lastNames.get(rand.nextInt(lastNames.size())),
						firstNames.get(rand.nextInt(firstNames.size())),
						middleInitial.get(rand.nextInt(middleInitial.size())), dept.getCode(), dept.getName(),
						salaries.get(rand.nextInt(salaries.size())));
				employees.add(emp);
			}
		}

		deptRepo.saveAll(depts);
		repo.saveAll(employees);
	}

	public static List<Employee> setupEmployees() {
		List<Employee> employees = new ArrayList();

		Employee emp;
		Random rand = new Random();
		Set<String> keySet = deptMap.keySet();
		int n = 50;
		for (String code : keySet) {
			int i = 0;
			while (i++ < n) {
				emp = create(lastNames.get(rand.nextInt(lastNames.size())),
						firstNames.get(rand.nextInt(firstNames.size())),
						middleInitial.get(rand.nextInt(middleInitial.size())), code, deptMap.get(code),
						salaries.get(rand.nextInt(salaries.size())));
				employees.add(emp);
			}
		}

		return employees;
	}

	public static Employee create(String lastName, String firstName, String middleInit, String deptCode,
			String departName, double salary) {
		Employee e = new Employee();
		e.setLastName(lastName);
		e.setFirstName(firstName);
		e.setMiddleInitial(middleInit);
		e.setDeptCode(deptCode);
		e.setDepartName(departName);
		e.setSalary(salary);
		return e;
	}

	private static Department create(String code, String name) {
		Department dept = new Department();
		dept.setCode(code);
		dept.setName(name);
		return dept;
	}

}
