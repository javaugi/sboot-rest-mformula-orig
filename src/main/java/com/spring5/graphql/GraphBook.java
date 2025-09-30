/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import java.util.Arrays;
import java.util.List;

public record GraphBook(String id, String name, int pageCount, String authorId) {

    private static List<GraphBook> books
            = Arrays.asList(
                    new GraphBook("book-1", "Effective Java", 416, "author-1"),
                    new GraphBook("book-2", "Hitchhiker's Guide to the Galaxy", 208, "author-2"),
                    new GraphBook("book-3", "Down Under", 436, "author-3"));

    public static GraphBook getById(String id) {
        return books.stream().filter(book -> book.id().equals(id)).findFirst().orElse(null);
    }
}
