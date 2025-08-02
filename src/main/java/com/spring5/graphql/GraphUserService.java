/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/* 
/graph/user/graphqls
type Query {
    allUsers: [GraphUser]
    userById(id: ID!): GraphUser
}

type GraphUser {
    id: ID!
    name: String!
    email: String!
}
 */
@Service
public class GraphUserService {
    
    private final Map<String, GraphUser> usersMap = new ConcurrentHashMap<>();
    
    
    private static final List<GraphUser> users = Arrays.asList(
            new GraphUser(1L, "Alice", "alice@example.com"),
            new GraphUser(2L, "Bob", "bob@example.com")
    );

    @PostConstruct
    public void init() {
        usersMap.put("1", new GraphUser(3L, "Alice Smith", "alice@example.com"));
        usersMap.put("2", new GraphUser(4L, "Bob Johnson", "bob@example.com"));
    }

    public List<GraphUser> getAllUsers() {
        return users;
    }

    public GraphUser getUserById(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }
    
    public List<GraphUser> findAllUsers() {
        return List.copyOf(usersMap.values());
    }

    public GraphUser findUserById(String id) {
        return usersMap.get(id);
    }    
}
   