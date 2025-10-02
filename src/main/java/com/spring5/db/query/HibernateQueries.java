/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.db.query;

import com.spring5.empbilpayroll.Department;
import com.spring5.empbilpayroll.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

/*
1. session.createQuery
2. session.getCriteriaBuilder()
3. session.createNativeQuery(sql, Employee.class);
4. session.createNamedQuery("Employee.findByDepartment", Employee.class);
5.  entityManager.createQuery(jpql, Employee.class);
    entityManager.getCriteriaBuilder();
    entityManager.createNativeQuery(qString);
 */
public class HibernateQueries {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SessionFactory sessionFactory;

	private Session session = null;

	public HibernateQueries() {
		session = sessionFactory.getCurrentSession();
		String sql = "";
		entityManager.createQuery(sql);
		entityManager.getCriteriaBuilder();
		entityManager.createNativeQuery(sql);
		entityManager.createNamedQuery(sql);
		entityManager.createNamedStoredProcedureQuery(sql);
	}

	private void hqlQ1BasicSel() {
		// Basic SELECT
		String hql = "FROM Employee";
		Query<Employee> query = session.createQuery(hql, Employee.class);
		List<Employee> employees = query.getResultList();
	}

	private void hqlQ2SelWhere() {
		// SELECT with WHERE clause
		String hql = "FROM Employee WHERE department = :dept";
		Query<Employee> query = session.createQuery(hql, Employee.class);
		query.setParameter("dept", "IT");
		List<Employee> itEmployees = query.getResultList();
	}

	private void hqlQ3SelWithJoin() {
		// JOIN example
		String hql = "SELECT e.name, d.name FROM Employee e JOIN e.department d";
		Query<Object[]> query = session.createQuery(hql, Object[].class);
		List<Object[]> results = query.getResultList();
	}

	private void hqlQ4SelWithAggregate() {
		// Aggregate functions
		String hql = "SELECT AVG(e.salary) FROM Employee e";
		Query<Double> query = session.createQuery(hql, Double.class);
		Double avgSalary = query.getSingleResult();
	}

	private void criteriaQ1Basic() {
		// Basic Criteria query
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Employee> criteria = builder.createQuery(Employee.class);
		Root<Employee> root = criteria.from(Employee.class);
		criteria.select(root);
		List<Employee> employees = session.createQuery(criteria).getResultList();
	}

	private void criteriaQ2WithConditions() {
		// Criteria with conditions
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Employee> criteria = builder.createQuery(Employee.class);
		Root<Employee> root = criteria.from(Employee.class);
		criteria.select(root).where(builder.equal(root.get("department"), "IT"));
		List<Employee> itEmployees = session.createQuery(criteria).getResultList();
	}

	private void criteriaQ2WithJoins() {
		// Join with Criteria
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteria = builder.createQuery(Object[].class);
		Root<Employee> emp = criteria.from(Employee.class);
		Join<Employee, Department> dept = emp.join("department");
		criteria.multiselect(emp.get("name"), dept.get("name"));
		List<Object[]> results = session.createQuery(criteria).getResultList();
	}

	private void nativeSqlQ1Basic() {
		// Basic native SQL
		String sql = "SELECT * FROM employees";
		Query<Employee> query = session.createNativeQuery(sql, Employee.class);
		List<Employee> employees = query.getResultList();
	}

	private void nativeSqlQ2WithParameters() {
		// Native SQL with parameters
		String sql = "SELECT * FROM employees WHERE department = :dept";
		Query<Employee> query = session.createNativeQuery(sql, Employee.class);
		query.setParameter("dept", "Finance");
		List<Employee> financeEmployees = query.getResultList();
	}

	private void nativeSqlQ3WithScalarResults() {
		// Native SQL with scalar results
		String sql = "SELECT COUNT(*) FROM employees";
		Query<Long> query = session.createNativeQuery(sql, Long.class);
		Long count = query.getSingleResult();
	}

	private void namedQueryQ1() {
		// Using named query
		Query<Employee> query = session.createNamedQuery("Employee.findByDepartment", Employee.class);
		query.setParameter("dept", "HR");
		List<Employee> hrEmployees = query.getResultList();
	}

	private void namedQueryQ1ForScalarResults() {
		// Using named query for scalar result
		Query<Long> countQuery = session.createNamedQuery("Employee.countAll", Long.class);
		Long totalEmployees = countQuery.getSingleResult();
	}

	private void JpaWithHibernateProvider() {
		// JPQL query
		String jpql = "SELECT e FROM Employee e WHERE e.salary > :minSalary";
		TypedQuery<Employee> query = entityManager.createQuery(jpql, Employee.class);
		query.setParameter("minSalary", 50000.0);
		List<Employee> wellPaidEmployees = query.getResultList();

		// Criteria API with JPA
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
		Root<Employee> employee = cq.from(Employee.class);
		cq.select(employee).where(cb.gt(employee.get("salary"), 50000));
		List<Employee> results = entityManager.createQuery(cq).getResultList();
	}

}
