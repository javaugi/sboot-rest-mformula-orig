/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author javau
 */
public class GraphAuthorMutationResolver implements GraphQLMutationResolver {
    @Autowired
    private GraphAuthorRepository repo;

    public GraphAuthor addPrescription(String id, String firstName, String lastName) {
        return repo.save(new GraphAuthor(id, firstName, lastName));
    }
    
}
