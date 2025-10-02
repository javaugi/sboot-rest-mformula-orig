/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.utils.common;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReactiveClaimEventRepository extends JpaRepository<ReactiveClaimEvent, Long> {

	public Optional<ReactiveClaimEvent> existsById(String id);

	@Query("select c from ReactiveClaimEvent c where c.patientId = :patientId and c.claimType = :claimType "
			+ "and c.claimDate between :from and :to")
	public List<ReactiveClaimEvent> findByPatientAndTypeBetween(String patientId, String claimType, LocalDate from,
			LocalDate to);

	public List<ReactiveClaimEvent> findByEncounterId(String encounterId);

}
