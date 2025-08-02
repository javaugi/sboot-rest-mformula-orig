/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;


@Controller
public class GraphBookFieldResolver {
    
    private final GraphAuthorService authorService;

    public GraphBookFieldResolver(GraphAuthorService authorService) {
        this.authorService = authorService;
    }

    // This method resolves the 'author' field within the 'Book' type.
    // The 'book' argument is the parent object (the Book that was just fetched).
    @SchemaMapping
    public GraphAuthor author(GraphBook book) {
        System.out.println("Resolving author for book ID: " + book.id() + " (authorId: " + book.authorId() + ")");
        return authorService.findAuthorById(book.authorId());
    }    
}
