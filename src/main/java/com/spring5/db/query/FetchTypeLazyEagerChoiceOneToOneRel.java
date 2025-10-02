/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.db.query;

import com.spring5.entity.Customer;
import com.stripe.model.ShippingDetails;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceContext;

/*
FetchType.EAGER in One-to-One Relationships: When It Makes Sense
    The choice between FetchType.EAGER and FetchType.LAZY for one-to-one relationships depends on several factors. Let me break down the
        considerations with concrete examples.
Default Behavior in JPA/Hibernate
    For one-to-one relationships:
        JPA default: FetchType.EAGER
        Hibernate default: Also FetchType.EAGER
When EAGER Loading Makes Sense
    1. When Child Data is Always Needed
    2. When Performance Impact is Minimal
When LAZY Loading is Better
    1. When Child Data is Large or Rarely Needed
    2. When You Want to Avoid Cartesian Products

Best Practice Approach
    1. Default to LAZY, Use Fetch Joins When Needed
    2. Consider @MapsId for Shared Primary Key

Performance Considerations
    EAGER Loading:
        Always loads the association
        Can cause Cartesian product issues in queries
        May load unnecessary data
    LAZY Loading:
        More efficient when association isn't always needed
        Requires proper session/transaction management
        May cause N+1 problems if not properly handled


Conclusion

Use FetchType.EAGER for one-to-one relationships when:
    The associated data is small and almost always needed
    The performance impact is negligible
    You're certain the data will be used in most cases

Otherwise, prefer FetchType.LAZY and:
    Use fetch joins when you need the data
    Consider batch fetching for multiple entities
    Use entity graphs for complex scenarios

Remember that with modern Hibernate versions, the performance difference between EAGER and properly managed LAZY loading with fetch joins is
    often minimal, while LAZY gives you more flexibility.
 */
public class FetchTypeLazyEagerChoiceOneToOneRel {

	@PersistenceContext
	private EntityManager entityManager;

	/*
	 * The Hibernate default FetchType is EAGER. This means that by default, when you load
	 * an entity that has a relationship
	 * (like @OneToOne, @ManyToOne, @OneToMany, @ManyToMany), Hibernate will attempt to
	 * load the associated entities immediately along with the primary entity.
	 */
	public void eagerWhenChildDataAlwaysNeeded() {
		/*
		 * @Entity public class User {
		 * 
		 * @Id
		 * 
		 * @GeneratedValue private Long id;
		 * 
		 * @OneToOne(fetch = FetchType.EAGER) // Makes sense here
		 * 
		 * @JoinColumn(name = "preferences_id") private UserPreferences preferences;
		 * 
		 * // Other fields }
		 * 
		 * @Entity public class UserPreferences {
		 * 
		 * @Id
		 * 
		 * @GeneratedValue private Long id;
		 * 
		 * private String theme; private String language;
		 * 
		 * @OneToOne(mappedBy = "preferences") private User user; }
		 */
	}

	private void eagerWhenPerformanceImpactMinimal() {
		// see below Order and shippingDetails
	}

	@Entity
	public class Order {

		@Id
		@GeneratedValue
		private Long id;

		@OneToOne(fetch = FetchType.EAGER) // Small table, always needed
		@JoinColumn(name = "shipping_details_id")
		private ShippingDetails shippingDetails;

	}

	private void lazyWhenChildDataLargeOrRarelyNeeded() {
		/*
		 * @Entity public class Product {
		 * 
		 * @Id
		 * 
		 * @GeneratedValue private Long id;
		 * 
		 * @OneToOne(fetch = FetchType.LAZY) // Better approach here
		 * 
		 * @JoinColumn(name = "product_manual_id") private ProductManual manual; }
		 */
	}

	private void lazyToAvoidCartesianProducts() {
		/*
		 * @Entity public class Employee {
		 * 
		 * @Id
		 * 
		 * @GeneratedValue private Long id;
		 * 
		 * @OneToOne(fetch = FetchType.LAZY) // Prevents data multiplication
		 * 
		 * @JoinColumn(name = "parking_spot_id") private ParkingSpot parkingSpot; }
		 */
	}

	private void bestAppraochDefLazyFetchJoinWhenNeeded() {
		// see method getCustomerWithLoyalty
	}

	// Then use fetch join when you need it
	public Customer getCustomerWithLoyalty(Long customerId) {
		return entityManager
			.createQuery("SELECT c FROM Customer c LEFT JOIN FETCH c.loyaltyAccount WHERE c.id = :id", Customer.class)
			.setParameter("id", customerId)
			.getSingleResult();
	}

	private void bestApproachMapsId4SharedPrimaryKey() {
		// see below Person and Persondetails
	}

	@Entity
	public class Person {

		@Id
		@GeneratedValue
		private Long id;

		@OneToOne(mappedBy = "person", fetch = FetchType.LAZY)
		private PersonDetails details;

	}

	@Entity
	public class PersonDetails {

		@Id
		private Long id; // Same as person ID

		@MapsId
		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "id")
		private Person person;

		// Other fields

	}

	private void realWorldEx() {
		/*
		 * @Entity public class BankAccount {
		 * 
		 * @Id
		 * 
		 * @GeneratedValue private Long id;
		 * 
		 * // EAGER makes sense - small table, always needed
		 * 
		 * @OneToOne(fetch = FetchType.EAGER)
		 * 
		 * @JoinColumn(name = "account_type_id") private AccountType type;
		 * 
		 * // LAZY better - large data, not always needed
		 * 
		 * @OneToOne(fetch = FetchType.LAZY)
		 * 
		 * @JoinColumn(name = "monthly_statement_id") private MonthlyStatement
		 * currentStatement; }
		 */
	}

}

/*
 * what is the hibernate default value for FetchType and how to use it properly
 * 
 * 
 * The Hibernate default FetchType is EAGER.
 * 
 * This means that by default, when you load an entity that has a relationship
 * (like @OneToOne, @ManyToOne, @OneToMany, @ManyToMany), Hibernate will attempt to load
 * the associated entities immediately along with the primary entity.
 * 
 * Understanding FetchType FetchType is an enumeration in JPA (and consequently Hibernate,
 * which is a JPA implementation) that determines when associated data should be retrieved
 * from the database. It has two main values:
 * 
 * EAGER (Default):
 * 
 * Meaning: The associated entity or collection is fetched immediately when the primary
 * entity is loaded.
 * 
 * Pros: All required data is available as soon as the initial entity is loaded, reducing
 * the chance of LazyInitializationException. Convenient for simple use cases.
 * 
 * Cons: Can lead to the "N+1 select problem" if not handled carefully, resulting in many
 * unnecessary database queries and poor performance, especially with collections. It can
 * also fetch more data than immediately needed, consuming more memory.
 * 
 * Default for:
 * 
 * @OneToOne
 * 
 * @ManyToOne
 * 
 * LAZY:
 * 
 * Meaning: The associated entity or collection is fetched only when it is explicitly
 * accessed for the first time. Hibernate will usually return a proxy object initially.
 * 
 * Pros: Improves performance by fetching only the data that is actually needed. Reduces
 * the impact of the N+1 select problem by deferring queries.
 * 
 * Cons: Can lead to LazyInitializationException if the associated data is accessed
 * outside of an active Hibernate session (e.g., after the session is closed). Requires
 * more careful management of session boundaries.
 * 
 * Default for:
 * 
 * @OneToMany
 * 
 * @ManyToMany
 * 
 * How to Use FetchType Properly Since the default is EAGER for single-valued associations
 * and LAZY for collection-valued associations, it's important to understand when and how
 * to override these defaults based on your application's needs.
 * 
 * 1. Explicitly Define FetchType in Annotations: You can override the default by
 * specifying fetch = FetchType.LAZY or fetch = FetchType.EAGER in your mapping
 * annotations.
 * 
 * Java
 * 
 * // User.java (owning side of ManyToOne)
 * 
 * @Entity public class User {
 * 
 * @Id private Long id; private String username;
 * 
 * // Default for ManyToOne is EAGER. Let's explicitly make it LAZY if we often don't need
 * the department.
 * 
 * @ManyToOne(fetch = FetchType.LAZY)
 * 
 * @JoinColumn(name = "department_id") private Department department;
 * 
 * // Getters and Setters }
 * 
 * // Department.java (owning side of OneToMany)
 * 
 * @Entity public class Department {
 * 
 * @Id private Long id; private String name;
 * 
 * // Default for OneToMany is LAZY. We might keep it LAZY to avoid fetching all users.
 * 
 * @OneToMany(mappedBy = "department", fetch = FetchType.LAZY, cascade = CascadeType.ALL,
 * orphanRemoval = true) private List<User> users = new ArrayList<>();
 * 
 * // Getters and Setters } 2. Prefer FetchType.LAZY for Performance: General Rule: It's
 * often recommended to start with FetchType.LAZY for all associations, especially
 * collection-valued ones (@OneToMany, @ManyToMany). This is the safest approach for
 * performance.
 * 
 * Why? LAZY fetching avoids loading unnecessary data, which can significantly reduce
 * memory consumption and the number of database queries, especially when dealing with
 * large datasets or complex object graphs.
 * 
 * 3. Strategically Use FetchType.EAGER (with caution): When to consider EAGER:
 * 
 * For very small, frequently accessed, and critical relationships where the associated
 * entity is almost always needed immediately when the parent is loaded.
 * 
 * When the associated entity is genuinely small and fetching it eagerly won't cause
 * significant performance overhead or N+1 issues.
 * 
 * To simplify application code by avoiding LazyInitializationException in very specific,
 * controlled scenarios where you're sure the session will be open.
 * 
 * Recommendation: Use EAGER sparingly and only after careful profiling.
 * 
 * 4. Handle LazyInitializationException: If you set FetchType.LAZY, you must ensure that
 * any access to the lazily loaded association happens within an active Hibernate session
 * (transaction).
 * 
 * Solutions:
 * 
 * Open-Session-in-View (OSIV) Pattern: A common pattern in web applications where the
 * Hibernate session remains open throughout the view rendering phase. While convenient,
 * it can mask underlying fetching issues and keep resources open longer than necessary.
 * 
 * Transactional Boundaries: Ensure that the code accessing the lazy association is part
 * of a transactional method. Spring's @Transactional annotation is excellent for this.
 * 
 * Explicitly Initialize: If you need the data outside the session, you can explicitly
 * initialize it before the session closes using methods like
 * Hibernate.initialize(entity.getLazyCollection()) or by performing a query that fetches
 * the data.
 * 
 * 5. Override Fetching Behavior at Query Time: This is the most flexible and recommended
 * approach for controlling fetching. You can define a default FetchType in your entity
 * mappings (often LAZY for performance), but then override it for specific queries using:
 * 
 * JOIN FETCH in JPQL/HQL:
 * 
 * Java
 * 
 * // Fetch users AND their departments in a single query List<User> users =
 * entityManager.createQuery( "SELECT u FROM User u JOIN FETCH u.department", User.class)
 * .getResultList(); This fetches the department eagerly for this specific query, even if
 * department is mapped as LAZY in the entity. It avoids the N+1 problem.
 * 
 * Entity Graphs (@NamedEntityGraph or programmatic EntityGraph): Entity Graphs provide a
 * powerful way to define fetching strategies for a query.
 * 
 * Java
 * 
 * // In your User entity
 * 
 * @NamedEntityGraph( name = "user-with-department", attributeNodes
 * = @NamedAttributeNode("department") ) public class User { // ...
 * 
 * @ManyToOne(fetch = FetchType.LAZY) // Still lazy by default
 * 
 * @JoinColumn(name = "department_id") private Department department; // ... }
 * 
 * // When querying EntityGraph<User> entityGraph =
 * entityManager.createEntityGraph(User.class); entityGraph.addSubgraph("department");
 * 
 * Map<String, Object> properties = new HashMap<>();
 * properties.put("javax.persistence.fetchgraph", entityGraph); // Or
 * "javax.persistence.loadgraph"
 * 
 * User user = entityManager.find(User.class, userId, properties); // department will be
 * fetched eagerly for this specific find operation fetchgraph ensures only specified
 * attributes are loaded (defaulting others to lazy), while loadgraph loads specified
 * attributes eagerly and uses default fetching for others.
 * 
 * Best Practices Summary: Default to LAZY for most associations in your entity mappings.
 * This sets you up for good performance by default.
 * 
 * Use JOIN FETCH or EntityGraph in your JPQL/HQL queries to eagerly fetch specific
 * associations when they are genuinely needed for a particular use case. This gives you
 * fine-grained control over fetching and helps avoid the N+1 problem.
 * 
 * Understand transactional boundaries to prevent LazyInitializationException when working
 * with LAZY fetched data.
 * 
 * Profile your application to identify performance bottlenecks and adjust fetching
 * strategies as needed. Don't guess; measure.
 */
