/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.genaihealthcare;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyProgramService {

	@Autowired
	private LoyaltyProgramRepository repository;

	public LoyaltyProgram enrollPatient(LoyaltyProgram program) {
		return repository.save(program);
	}

	public List<LoyaltyProgram> getPatientPrograms(String patientId) {
		return repository.findByPatientId(patientId);
	}

	public LoyaltyProgram updatePoints(String patientId, String programName, Integer points) {
		LoyaltyProgram program = repository.findByPatientId(patientId)
			.stream()
			.filter(p -> p.getProgramName().equals(programName))
			.findFirst()
			.orElse(null);

		if (program != null) {
			program.setPointsBalance(program.getPointsBalance() + points);
			return repository.save(program);
		}

		return null;
	}

	public Long getProgramEnrollmentCount(String programName) {
		return repository.countByProgramName(programName);
	}

}
