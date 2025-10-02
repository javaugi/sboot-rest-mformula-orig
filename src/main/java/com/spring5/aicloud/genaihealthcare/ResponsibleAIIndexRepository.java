/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsibleAIIndexRepository extends JpaRepository<ResponsibleAIIndex, Long> {

	List<ResponsibleAIIndex> findByComplianceStatus(String complianceStatus);

	@Query("SELECT r FROM ResponsibleAIIndex r WHERE r.overallScore >= :minScore")
	List<ResponsibleAIIndex> findByMinOverallScore(Double minScore);

	ResponsibleAIIndex findByModelName(String modelName);

}
