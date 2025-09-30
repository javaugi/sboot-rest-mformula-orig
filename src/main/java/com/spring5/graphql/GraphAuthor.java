/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import java.util.Arrays;
import java.util.List;

public record GraphAuthor(String id, String firstName, String lastName) {

    private static List<GraphAuthor> authors
            = Arrays.asList(
                    new GraphAuthor("author-1", "Joshua", "Bloch"),
                    new GraphAuthor("author-2", "Douglas", "Adams"),
                    new GraphAuthor("author-3", "Bill", "Bryson"));

    public static GraphAuthor getById(String id) {
        return authors.stream().filter(author -> author.id().equals(id)).findFirst().orElse(null);
    }
}
