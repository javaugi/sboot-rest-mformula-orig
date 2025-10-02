/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphqlserver;

import com.spring5.graphql.GraphAuthor;
import com.spring5.graphql.GraphBook;
import com.spring5.graphql.GraphBookController;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(GraphBookController.class)
public class GraphBookControllerTests {

	private static List<GraphAuthor> authors = Arrays.asList(new GraphAuthor("author-1", "Joshua", "Bloch"),
			new GraphAuthor("author-2", "Douglas", "Adams"), new GraphAuthor("author-3", "Bill", "Bryson"));

	private static List<GraphBook> books = Arrays.asList(new GraphBook("book-1", "Effective Java", 416, "author-1"),
			new GraphBook("book-2", "Hitchhiker's Guide to the Galaxy", 208, "author-2"),
			new GraphBook("book-3", "Down Under", 436, "author-3"));

	// *
	@Autowired
	private GraphQlTester graphQlTester;

	// @Test
	public void shouldGetFirstBook() {
		this.graphQlTester.documentName("bookDetails")
			.variable("id", "book-1")
			.execute()
			.path("bookById")
			.matchesJson("""
					    {
					        "id": "book-1",
					        "name": "Effective Java",
					        "pageCount": 416,
					        "author": {
					          "firstName": "Joshua",
					          "lastName": "Bloch"
					        }
					    }
					""");
	}
	// */

}
