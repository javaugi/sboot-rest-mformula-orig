/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.entity.arts;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

	@Query("SELECT COUNT(ae) > 0 FROM Artist ae  WHERE ae.firstName =(:firstName) and ae.lastName =(:lastName)")
	boolean existsByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

	@Query("SELECT ae FROM Artist ae  WHERE ae.firstName =(:firstName) and ae.lastName =(:lastName)")
	Optional<Artist> findByFirstNameAndLastName(@Param("firstName") String firstName,
			@Param("lastName") String lastName);

}
