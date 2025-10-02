/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/*
The @QueryMapping annotation binds this method to a query, a field under the Query type. The query field is then
    determined from the method name, bookById. It could also be declared on the annotation. Spring for GraphQL uses
    RuntimeWiring.Builder to register the handler method as a graphql.schema.DataFetcher for the query field bookById.

In GraphQL Java, DataFetchingEnvironment provides access to a map of field-specific argument values. Use the @Argument
    annotation to have an argument bound to a target object and injected into the handler method. By default, the method
    parameter name is used to look up the argument. The argument name can be specified in the annotation.

The @SchemaMapping annotation maps a handler method to a field in the GraphQL schema and declares it to be the DataFetcher
    for that field. The field name defaults to the method name, and the type name defaults to the simple class name of the
    source/parent object injected into the method. In this example, the field defaults to author and the type defaults to Book.
    The type and field can be specified in the annotation.
 */
@Controller
@RequiredArgsConstructor
public class GraphBookController {

	private final GraphUserService userService;

	private final GraphBookService bookService;

	@SchemaMapping
	public GraphAuthor author(@Argument GraphBook book) {
		return GraphAuthor.getById(book.authorId());
	}

	// Maps to 'bookById(id: ID!)' field in Query type
	// Maps to 'bookById(id: ID!)' field in Query type
	@QueryMapping
	public GraphBook bookById(@Argument String id) {
		System.out.println("Fetching book by ID: " + id);
		return bookService.findBookById(id);
	}

	/*
	 * @QueryMapping public GraphBook bookById(@Argument String id) {
	 * System.out.println("Fetching book by ID: " + id); return GraphBook.getById(id); }
	 * //
	 */
	// Maps to 'allUsers' field in Query type
	@QueryMapping
	public List<GraphUser> allUsers() {
		System.out.println("Fetching all users...");
		return userService.findAllUsers();
	}

	// Maps to 'userById(id: ID!)' field in Query type
	@QueryMapping
	public GraphUser userById(@Argument String id) {
		System.out.println("Fetching user by ID: " + id);
		return userService.findUserById(id);
	}

}

/*
 * 
 * Boot the application​ Start your Spring application.
 * 
 * Navigate to http://localhost:8080/graphiql or your custom URL.
 * 
 * Run the query​ Type in the query and hit the play button at the top of the window.
 * 
 * example query - put the cursor in front the word query and then click the submit arrow
 * 
 * query bookDetails { bookById(id: "book-1"){ id name pageCount author { firstName
 * lastName } } }
 * 
 * 
 */

/*
 * Understanding Spring Boot GraphQL: QueryMapping vs. Schema Mapping In Spring for
 * GraphQL, which integrates Spring Boot with GraphQL, the terms "QueryMapping" and
 * "schema mapping" (often referred to as data fetchers or field resolvers) describe how
 * you connect your GraphQL schema definitions to your backend Java code.
 * 
 * 1. GraphQL Schema Review and Correction Let's first review your provided schema:
 * 
 * type Query { allUsers: [GraphUser] userById(id: ID!): GraphUser
 * 
 * bookById(id: ID!): GraphBook }
 * 
 * type GraphUser { id: ID! name: String! email: String! }
 * 
 * type GraphBook { id: ID! name: String pageCount: Int author: GraphAuthor }
 * 
 * type GraphAuthor { id: ID! firstName: String lastName: String }
 * 
 * Schema Observations/Suggestions:
 * 
 * GraphUser and GraphBook name fields:
 * 
 * GraphUser has name: String! (non-nullable), which is good for mandatory fields.
 * 
 * GraphBook has name: String (nullable). Is the book name always required? If so,
 * consider name: String!.
 * 
 * GraphAuthor firstName and lastName: These are nullable (String). Depending on your data
 * model, you might want them to be non-nullable if an author always has a first and last
 * name (firstName: String!, lastName: String!).
 * 
 * Consistency in Naming: While GraphUser, GraphBook, GraphAuthor is clear, sometimes
 * shorter names like User, Book, Author are used if they are exclusively GraphQL types.
 * This is a style preference, not an error.
 * 
 * Corrected/Improved Schema (Optional but Recommended):
 * 
 * Assuming book name, author firstName, and author lastName should be non-nullable:
 * 
 * type Query { allUsers: [User] userById(id: ID!): User
 * 
 * bookById(id: ID!): Book }
 * 
 * type User { id: ID! name: String! email: String! }
 * 
 * type Book { id: ID! name: String! # Changed to non-nullable pageCount: Int author:
 * Author # Consider if 'author' should be non-nullable if a book always has one. }
 * 
 * type Author { id: ID! firstName: String! # Changed to non-nullable lastName: String! #
 * Changed to non-nullable }
 * 
 * For the examples below, I will use the improved schema with User, Book, Author for
 * brevity and clarity, but the principles apply to your original GraphUser etc. names
 * too.
 * 
 * 2. QueryMapping in Spring Boot for GraphQL
 * 
 * @QueryMapping is a Spring for GraphQL annotation used to map top-level Query (and
 * Mutation, Subscription) fields in your GraphQL schema directly to handler methods in
 * your Spring controllers or services.
 * 
 * Essentially, when a GraphQL query requests one of the top-level fields defined in your
 * type Query (like allUsers, userById, bookById), Spring for GraphQL looks for a method
 * annotated with @QueryMapping that matches that field name.
 * 
 * Example QueryMapping Implementation:
 * 
 * First, let's define simple Java data classes that correspond to your GraphQL types:
 * 
 * // src/main/java/com/example/graphql/data/User.java package com.example.graphql.data;
 * 
 * public record User(String id, String name, String email) {}
 * 
 * // src/main/java/com/example/graphql/data/Book.java package com.example.graphql.data;
 * 
 * public record Book(String id, String name, Integer pageCount, String authorId) {} //
 * Note: authorId here, not Author object yet
 * 
 * // src/main/java/com/example/graphql/data/Author.java package com.example.graphql.data;
 * 
 * public record Author(String id, String firstName, String lastName) {}
 * 
 * Now, let's create a Spring @Controller (or @Service) to handle the top-level queries:
 * 
 * // src/main/java/com/example/graphql/controller/GraphQLQueryController.java package
 * com.example.graphql.controller;
 * 
 * import com.example.graphql.data.Book; import com.example.graphql.data.User; import
 * com.example.graphql.service.BookService; import
 * com.example.graphql.service.UserService; import
 * org.springframework.graphql.data.method.annotation.Argument; import
 * org.springframework.graphql.data.method.annotation.QueryMapping; import
 * org.springframework.stereotype.Controller;
 * 
 * import java.util.List;
 * 
 * @Controller public class GraphQLQueryController {
 * 
 * private final UserService userService; private final BookService bookService;
 * 
 * public GraphQLQueryController(UserService userService, BookService bookService) {
 * this.userService = userService; this.bookService = bookService; }
 * 
 * // Maps to 'allUsers' field in Query type
 * 
 * @QueryMapping public List<User> allUsers() {
 * System.out.println("Fetching all users..."); return userService.findAllUsers(); }
 * 
 * // Maps to 'userById(id: ID!)' field in Query type
 * 
 * @QueryMapping public User userById(@Argument String id) {
 * System.out.println("Fetching user by ID: " + id); return userService.findUserById(id);
 * }
 * 
 * // Maps to 'bookById(id: ID!)' field in Query type
 * 
 * @QueryMapping public Book bookById(@Argument String id) {
 * System.out.println("Fetching book by ID: " + id); return bookService.findBookById(id);
 * } }
 * 
 * And simple service classes (for mock data):
 * 
 * // src/main/java/com/example/graphql/service/UserService.java package
 * com.example.graphql.service;
 * 
 * import com.example.graphql.data.User; import org.springframework.stereotype.Service;
 * 
 * import java.util.Arrays; import java.util.List; import java.util.Map; import
 * java.util.concurrent.ConcurrentHashMap;
 * 
 * @Service public class UserService { private final Map<String, User> users = new
 * ConcurrentHashMap<>();
 * 
 * public UserService() { users.put("1", new User("1", "Alice Smith",
 * "alice@example.com")); users.put("2", new User("2", "Bob Johnson", "bob@example.com"));
 * }
 * 
 * public List<User> findAllUsers() { return List.copyOf(users.values()); }
 * 
 * public User findUserById(String id) { return users.get(id); } }
 * 
 * // src/main/java/com/example/graphql/service/BookService.java package
 * com.example.graphql.service;
 * 
 * import com.example.graphql.data.Book; import org.springframework.stereotype.Service;
 * 
 * import java.util.Arrays; import java.util.List; import java.util.Map; import
 * java.util.concurrent.ConcurrentHashMap;
 * 
 * @Service public class BookService { private final Map<String, Book> books = new
 * ConcurrentHashMap<>();
 * 
 * public BookService() { books.put("B1", new Book("B1", "The Great Novel", 300, "A1"));
 * books.put("B2", new Book("B2", "Spring Boot Guide", 500, "A2")); }
 * 
 * public List<Book> findAllBooks() { return List.copyOf(books.values()); }
 * 
 * public Book findBookById(String id) { return books.get(id); } }
 * 
 * How it works:
 * 
 * When a client sends a query like { allUsers { name } }, Spring for GraphQL routes it to
 * the allUsers() method in GraphQLQueryController.
 * 
 * The @Argument annotation is used to extract arguments from the GraphQL query (e.g., id
 * for userById).
 * 
 * 3. "Schema Mapping" (Data Fetchers / Field Resolvers) "Schema mapping" refers to how
 * you resolve fields within a type that are not directly available from the parent
 * object. This is often handled by Data Fetchers or Field Resolvers.
 * 
 * In your schema, the author: Author field within the Book type is a classic example.
 * When you fetch a Book object, you might get its basic properties (id, name, pageCount,
 * and perhaps an authorId). However, the author field itself is of type Author, meaning
 * it needs to resolve to an Author object, not just an ID.
 * 
 * Spring for GraphQL provides the @SchemaMapping annotation for this purpose. It allows
 * you to specify a method that will be invoked to resolve a specific field within a
 * specific type.
 * 
 * Example SchemaMapping Implementation for author field:
 * 
 * Let's enhance our BookService and add an AuthorService:
 * 
 * // src/main/java/com/example/graphql/service/AuthorService.java package
 * com.example.graphql.service;
 * 
 * import com.example.graphql.data.Author; import org.springframework.stereotype.Service;
 * 
 * import java.util.Map; import java.util.concurrent.ConcurrentHashMap;
 * 
 * @Service public class AuthorService { private final Map<String, Author> authors = new
 * ConcurrentHashMap<>();
 * 
 * public AuthorService() { authors.put("A1", new Author("A1", "Jane", "Doe"));
 * authors.put("A2", new Author("A2", "John", "Smith")); }
 * 
 * public Author findAuthorById(String id) { return authors.get(id); } }
 * 
 * Now, let's create a dedicated controller (or add to an existing one) to resolve the
 * author field for Book objects:
 * 
 * // src/main/java/com/example/graphql/controller/BookFieldResolver.java package
 * com.example.graphql.controller;
 * 
 * import com.example.graphql.data.Author; import com.example.graphql.data.Book; import
 * com.example.graphql.service.AuthorService; import
 * org.springframework.graphql.data.method.annotation.SchemaMapping; import
 * org.springframework.stereotype.Controller;
 * 
 * @Controller public class BookFieldResolver {
 * 
 * private final AuthorService authorService;
 * 
 * public BookFieldResolver(AuthorService authorService) { this.authorService =
 * authorService; }
 * 
 * // This method resolves the 'author' field within the 'Book' type. // The 'book'
 * argument is the parent object (the Book that was just fetched).
 * 
 * @SchemaMapping public Author author(Book book) {
 * System.out.println("Resolving author for book ID: " + book.id() + " (authorId: " +
 * book.authorId() + ")"); return authorService.findAuthorById(book.authorId()); } }
 * 
 * How it works:
 * 
 * When a GraphQL query asks for a Book and includes its author field (e.g., {
 * bookById(id: "B1") { name author { firstName } } }), Spring for GraphQL first fetches
 * the Book object using the @QueryMapping method (bookById).
 * 
 * Then, for each Book object, it notices that the author field needs to be resolved. It
 * looks for a method annotated with @SchemaMapping that takes a Book object as its
 * argument (or a specific field name using @SchemaMapping(typeName="Book",
 * field="author") if the method name doesn't match the field name).
 * 
 * The author(Book book) method is invoked, and the Book object (which contains authorId)
 * is passed to it.
 * 
 * The method then uses the authorId to fetch the complete Author object from the
 * AuthorService and returns it.
 * 
 * This deferred resolution is a core concept of GraphQL, allowing clients to request
 * exactly what they need and avoiding over-fetching data.
 * 
 * Summary of Differences: Feature
 * 
 * QueryMapping
 * 
 * SchemaMapping (Field Resolver)
 * 
 * Purpose
 * 
 * Maps top-level queries (or mutations/subscriptions) from the Query (or Mutation,
 * Subscription) type to handler methods.
 * 
 * Maps fields within an existing type (e.g., author field within Book) to a method that
 * resolves that specific field.
 * 
 * Trigger
 * 
 * When a client requests a top-level query (e.g., allUsers, userById).
 * 
 * When a client requests a field that is part of a complex type (e.g., requesting author
 * from a Book object).
 * 
 * Input
 * 
 * Takes @Argument values directly from the query.
 * 
 * Takes the parent object (e.g., Book object) as an argument, from which it can extract
 * necessary information to resolve the field.
 * 
 * Location
 * 
 * Often in a main GraphQLQueryController.
 * 
 * Can be in separate controllers/components dedicated to resolving fields for specific
 * types.
 * 
 * In essence, QueryMapping gets you the initial "root" data, while SchemaMapping helps
 * you "drill down" and fetch related data for nested fields as needed. They work together
 * to fully implement your GraphQL schema.
 */
