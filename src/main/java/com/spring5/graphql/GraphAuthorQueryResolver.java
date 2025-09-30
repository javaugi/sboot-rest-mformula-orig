/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author javau
 */
public class GraphAuthorQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private GraphAuthorRepository repo;

    public GraphAuthor getGraphAuthor(String id) {
        return repo.findById(id).orElseThrow();
    }
}
