/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.graphql;

import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface Neo4jPersonRepository extends Neo4jRepository<Neo4jPerson, Long> {

	Neo4jPerson findByName(@Param("name") String name);
	List<Neo4jPerson> findByTeammatesName(@Param("name") String name);
}
