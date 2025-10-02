/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.repository;

import com.spring5.entity.BonusMultiplier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BonusRepository extends JpaRepository<BonusMultiplier, Long> {

	// This method declaration is all you need!
	// Spring Data JPA automatically generates the query:
	// "SELECT b FROM BonusMultiplier b WHERE b.score = ?1"
	Optional<BonusMultiplier> findByScore(String score);

	// Other useful methods you get for free by extending JpaRepository:
	// - save(S entity)
	// - findById(ID id)
	// - findAll()
	// - deleteById(ID id)
	// - count()
	// ...and many more.

}
