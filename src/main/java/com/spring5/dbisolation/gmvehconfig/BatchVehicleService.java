/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmvehconfig;

import com.spring5.entity.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BatchVehicleService {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void batchInsert(List<Vehicle> vehicles) {
		int batchSize = 50;
		for (int i = 0; i < vehicles.size(); i++) {
			entityManager.persist(vehicles.get(i));
			if (i > 0 && i % batchSize == 0) {
				entityManager.flush();
				entityManager.clear(); // prevents memory issues
			}
		}
		entityManager.flush();
		entityManager.clear();
	}

	@Transactional
	public void batchUpdate(List<Vehicle> vehicles) {
		int batchSize = 50;
		for (int i = 0; i < vehicles.size(); i++) {
			Vehicle vehicle = vehicles.get(i);
			Vehicle existing = entityManager.find(Vehicle.class, vehicle.getId());
			if (existing != null) {
				existing.setTargetVersionId(vehicle.getTargetVersionId());
				// or other fields to update
			}

			if (i > 0 && i % batchSize == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}
		entityManager.flush();
		entityManager.clear();
	}

}
