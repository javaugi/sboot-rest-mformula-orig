/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class GraphBookService {

	private final Map<String, GraphBook> books = new ConcurrentHashMap<>();

	public GraphBookService() {
		books.put("B1", new GraphBook("B1", "The Great Novel", 300, "A1"));
		books.put("B2", new GraphBook("B2", "Spring Boot Guide", 500, "A2"));
	}

	public List<GraphBook> findAllBooks() {
		return List.copyOf(books.values());
	}

	public GraphBook findBookById(String id) {
		return books.get(id);
	}

}
