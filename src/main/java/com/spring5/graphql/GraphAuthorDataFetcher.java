/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphAuthorDataFetcher implements DataFetcher<List<GraphAuthor>> {

	@Autowired
	private GraphAuthorRepository repo;

	@Override
	public List<GraphAuthor> get(DataFetchingEnvironment env) {
		GraphAuthor author = env.getSource();
		author = repo.findById(author.id()).orElseThrow();
		return new ArrayList<>(List.of(author));
	}

}
