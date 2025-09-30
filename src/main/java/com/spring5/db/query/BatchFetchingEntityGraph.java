/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.db.query;

import com.spring5.empbilpayroll.Department;
import com.spring5.empbilpayroll.Employee;
import com.spring5.empbilpayroll.EmployeeProject;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Subgraph;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.jpa.QueryHints;

/*
Key Differences and When to Use Each
    Feature         Batch Fetching                      Entity Graphs
    Control         Automatic batch loading             Explicit fetch control
    Use Case        General N+1 prevention              Precise query optimization
    Performance     Good for many small associations	Better for complex deep graphs
    Configuration	Annotation-based or query hint      Can be named or dynamic

Best Practice Recommendation:
    Use batch fetching as a general solution for lazy loading performance
    Use entity graphs when you need precise control over specific queries
    Combine both for complex applications where different scenarios need different optimization approaches
 */
public class BatchFetchingEntityGraph {

    @PersistenceContext
    private EntityManager entityManager;

    private void basicBatchFetch() {
        /* see User
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = UserAccount.class)
        @BatchSize(size = 20)
        private List<UserAccount> userAccounts;
         */
        // Without batch fetching (N+1 problem)
        List<Department> departments
                = entityManager.createQuery("SELECT d FROM Department d", Department.class).getResultList();

        // When accessing employees, Hibernate will use batch loading
        for (Department dept : departments) {
            // Instead of one query per department, Hibernate will load in batches of 10
            System.out.println(dept.getName() + " has " + dept.getEmployees().size() + " employees");
        }
    }

    private void batchFetchProgrammaticaly() {
        // Using HINT_BATCH_SIZE hint in query
        List<Employee> employees
                = entityManager
                        .createQuery("SELECT e FROM Employee e WHERE e.department IS NOT NULL", Employee.class)
                        .setHint(QueryHints.HINT_FETCH_SIZE, "5")
                        .getResultList();

        // When accessing department for each employee, loads in batches of 5
        for (Employee emp : employees) {
            System.out.println(emp.getName() + " works in " + emp.getDepartment().getName());
        }
    }

    private void staticEntityGraphDefinition() {
        // Using named entity graph
        // @SuppressWarnings("unchecked") // Suppress unchecked cast warning
        // EntityGraph<Employee> graph =
        // entityManager.createEntityGraph("employee.withDepartmentAndProfile");

        EntityGraph<Employee> graph = entityManager.createEntityGraph(Employee.class);
        // Now you can add attributes to this graph
        graph.addSubgraph("department");
        graph.addSubgraph("profile");

        List<Employee> employees
                = entityManager
                        .createQuery("SELECT e FROM Employee e WHERE e.salary > :minSalary", Employee.class)
                        .setParameter("minSalary", 50000)
                        .setHint("javax.persistence.fetchgraph", graph)
                        .getResultList();

        // Department and profile are already loaded
        employees.forEach(
                emp -> {
                    System.out.println(
                            emp.getName()
                            + " in "
                            + emp.getDepartment().getName()
                            + " has profile: "
                            + emp.getProfile().getBio());
                });

        /*
    @Entity
    @NamedEntityGraphs({
        @NamedEntityGraph(
            name = "employee.withDepartmentAndProfile",
            attributeNodes = {
                @NamedAttributeNode("department"),
                @NamedAttributeNode("profile")
            }
        ),
        @NamedEntityGraph(
            name = "employee.withAllRelations",
            attributeNodes = {
                @NamedAttributeNode("department"),
                @NamedAttributeNode("profile"),
                @NamedAttributeNode(value = "projects", subgraph = "projectSubgraph")
            },
            subgraphs = {
                @NamedSubgraph(
                    name = "projectSubgraph",
                    attributeNodes = {
                        @NamedAttributeNode("tasks"),
                        @NamedAttributeNode("teamMembers")
                    }
                )
            }
        )
    })
    public class Employee {
        // ... fields ...
    }
         */
    }

    private void dynamicEntityGraph() {
        // Create entity graph dynamically
        EntityGraph<Employee> dynamicGraph = entityManager.createEntityGraph(Employee.class);
        dynamicGraph.addAttributeNodes("department");
        Subgraph<EmployeeProject> projectSubgraph = dynamicGraph.addSubgraph("projects");
        projectSubgraph.addAttributeNodes("tasks");

        // Use in query
        List<Employee> employees
                = entityManager
                        .createQuery("SELECT e FROM Employee e WHERE e.joinDate > :date", Employee.class)
                        .setParameter("date", LocalDate.now().minusYears(1))
                        .setHint("jakarta.persistence.fetchgraph", dynamicGraph)
                        .getResultList();
    }
}
