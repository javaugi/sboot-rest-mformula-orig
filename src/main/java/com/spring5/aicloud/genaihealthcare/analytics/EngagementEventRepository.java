package com.spring5.aicloud.genaihealthcare.analytics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngagementEventRepository extends JpaRepository<PatientEngagementEvent, Long> {

	List<PatientEngagementEvent> findByPatientId(String patientId);

}
