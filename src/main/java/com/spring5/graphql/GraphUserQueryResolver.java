/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/*

 */
@Controller
public class GraphUserQueryResolver {

	private final GraphUserService userService;

	public GraphUserQueryResolver(GraphUserService userService) {
		this.userService = userService;
	}

	@QueryMapping
	public List<GraphUser> allUsers() {
		return userService.getAllUsers();
	}

	@QueryMapping
	public GraphUser userById(@Param("id") Long id) {
		return userService.getUserById(id);
	}

}
