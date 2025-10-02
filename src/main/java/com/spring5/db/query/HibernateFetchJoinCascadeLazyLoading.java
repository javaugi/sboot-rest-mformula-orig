/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.db.query;

import com.spring5.empbilpayroll.Department;
import com.spring5.empbilpayroll.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/*
2. Understanding the Concepts
    FetchType (LAZY vs EAGER)
        LAZY: Load the association only when it's first accessed (default for collections)
        EAGER: Load the association immediately with the parent entity (default for single-valued associations)

    CascadeType - Defines which operations should cascade from the parent entity to the associated entities:
        PERSIST: When parent is saved, child is also saved
        MERGE: When parent is merged, child is also merged
        REMOVE: When parent is deleted, child is also deleted
        REFRESH: When parent is refreshed, child is also refreshed
        ALL: All of the above
Fetch Join
    A technique to override the FetchType for a specific query to avoid N+1 problems.
3. Query Examples Showing Proper Usage
    Without Fetch Join (N+1 Problem)
    With Fetch Join (Solves N+1)
 */
public class HibernateFetchJoinCascadeLazyLoading {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SessionFactory sessionFactory;

	private Session session = null;

	public HibernateFetchJoinCascadeLazyLoading() {
		session = sessionFactory.getCurrentSession();
		/*
		 * String sql = ""; entityManager.createQuery(sql);
		 * entityManager.getCriteriaBuilder(); entityManager.createNativeQuery(sql);
		 * entityManager.createNamedQuery(sql);
		 * entityManager.createNamedStoredProcedureQuery(sql); //
		 */
	}

	// Without Fetch Join (N+1 Problem)
	private void n1ProblemWithoutFetchJoin() {
		// This will cause N+1 queries (1 for departments + N for employees of each
		// department)
		List<Department> departments = session.createQuery("FROM Department d", Department.class).getResultList();

		// When you access employees, a new query is executed for each department
		for (Department dept : departments) {
			System.out.println("Department: " + dept.getName());
			for (Employee emp : dept.getEmployees()) { // Triggers new query
				System.out.println(" - Employee: " + emp.getName());
			}
		}
	}

	// With Fetch Join (Solves N+1)
	private void n1ProblemResolvedWithFetchJoin() {
		// Single query that fetches both departments and their employees
		List<Department> departments = session
			.createQuery("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees", Department.class)
			.getResultList();

		// No additional queries when accessing employees
		for (Department dept : departments) {
			System.out.println("Department: " + dept.getName());
			for (Employee emp : dept.getEmployees()) { // Already loaded
				System.out.println(" - Employee: " + emp.getName());
			}
		}
	}

	// Cascade Type in Action
	@Transactional
	public void createDepartmentWithEmployees() {
		Department itDept = new Department();
		itDept.setName("IT");

		Employee emp1 = new Employee();
		emp1.setName("John Doe");
		emp1.setDepartment(itDept);

		Employee emp2 = new Employee();
		emp2.setName("Jane Smith");
		emp2.setDepartment(itDept);

		itDept.getEmployees().add(emp1);
		itDept.getEmployees().add(emp2);

		// Because of CascadeType.ALL, saving department will save employees too
		session.persist(itDept);

		// No need to explicitly persist emp1 and emp2
	}

	// Lazy Loading with Proper Session Management
	// Service method with open session
	@Transactional
	public void printDepartmentEmployees(Long deptId) {
		Department department = session.get(Department.class, deptId);

		// Employees are loaded lazily, but since we're in a transaction, it works
		System.out.println("Employees in " + department.getName() + ":");
		for (Employee emp : department.getEmployees()) {
			System.out.println(" - " + emp.getName());
		}
	}

	// Without transaction (would throw LazyInitializationException)
	public void printDepartmentEmployeesProblematic(Long deptId) {
		Department department = session.get(Department.class, deptId);
		session.close(); // Session is closed

		// This will throw exception because employees weren't loaded
		for (Employee emp : department.getEmployees()) {
			System.out.println(" - " + emp.getName());
		}
	}

	private void bestPracticeWithcascadeFetchMode() {
		/*
		 * 4. Best Practices Default to LAZY loading for all associations to avoid loading
		 * unnecessary data Use fetch joins when you know you'll need the associated data
		 * Be careful with CascadeType.ALL - it can lead to unintended deletions Manage
		 * transactions properly to avoid LazyInitializationException Consider batch
		 * fetching for large collections: //
		 */
		/*
		 * @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
		 * 
		 * @BatchSize(size = 10) private List<Employee> employees; //
		 */
	}

	/*
	 * Remember that fetch joins and cascade types serve different purposes: Fetch joins
	 * are about query performance (loading strategy for a specific use case) Cascade
	 * types are about object lifecycle management (what operations should propagate) Lazy
	 * loading is about default loading behavior (when data should be loaded)
	 */
	private void complexQueryWithMultipleFetchJoins() {
		// Complex query with multiple fetch joins
		List<Department> departments = session
			.createQuery("SELECT DISTINCT d FROM Department d " + "LEFT JOIN FETCH d.employees e "
					+ "LEFT JOIN FETCH e.profile " + "WHERE d.name LIKE :deptName", Department.class)
			.setParameter("deptName", "%IT%")
			.getResultList();
	}

	// Transactional method showing cascading behavior
	@Transactional
	public void updateDepartmentWithEmployees(Long deptId) {
		Department department = session.find(Department.class, deptId);

		// Because of CascadeType.ALL, these changes will be cascaded
		department.getEmployees().forEach(emp -> {
			emp.setSalary(emp.getSalary() * 1.1); // Give everyone a 10% raise
		});

		// No need to explicitly save each employee
	}

}
