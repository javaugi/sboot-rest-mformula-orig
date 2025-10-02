/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/azureapi/prescriptions")
public class PrescriptionController {

	private final PrescriptionRepository prescriptionRepository;

	@Autowired
	public PrescriptionController(PrescriptionRepository prescriptionRepository) {
		this.prescriptionRepository = prescriptionRepository;
	}

	@PostMapping
	public ResponseEntity<PrescriptionOrder> createPrescription(@RequestBody PrescriptionOrder order) {
		if (order.getId() == null || order.getId().isEmpty()) {
			order.setId(UUID.randomUUID().toString()); // Generate a unique ID if not
														// provided
		}
		if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
			order.setOrderId(order.getId()); // Use ID as orderId if not explicitly set
												// for partition key demo
		}
		if (order.getOrderDate() == null) {
			order.setOrderDate(Instant.now());
		}
		PrescriptionOrder savedOrder = prescriptionRepository.save(order);
		return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PrescriptionOrder> getPrescriptionById(@PathVariable String id) {
		Optional<PrescriptionOrder> order = prescriptionRepository.findById(id);
		return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<PrescriptionOrder>> getAllPrescriptions() {
		List<PrescriptionOrder> orders = (List<PrescriptionOrder>) prescriptionRepository.findAll();
		return ResponseEntity.ok(orders);
	}

	// You might also have methods for updating, deleting, or custom queries

}
