/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.gmcodility;

// VehicleTestResultController.java
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/*
Requirements:
    Build a Spring Boot endpoint that returns vehicle test results
    Support filtering by test type, date range, and pass/fail status
    Implement proper error handling
    Include unit tests

 */
@RestController
@RequestMapping("/api/vehicle-tests")
public class VehicleTestResultController {

	@Autowired
	private VehicleTestResultRepository testResultRepository;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<VehicleTestResult> createArtistByResp(@RequestBody VehicleTestResult testResult) {
		if (testResult.getTestType() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test Type is required");
		}

		// Check for duplicate artist
		if (testResult.getTestDate() == null) {
			System.out.println("createArtistByResp throws ResponseStatusException HttpStatus.CONFLICT ");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test Date Required");
		}

		return new ResponseEntity<>(testResultRepository.save(testResult), HttpStatus.CREATED);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<VehicleTestResult>> getTestResults(@RequestParam(required = false) String testType,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Boolean passed) {

		try {
			// Specification<VehicleTestResult> spec = where(null);
			// Specification<VehicleTestResult> spec = Specification.where(null);
			Specification<VehicleTestResult> spec = (root, query, criteriaBuilder) -> null; // Null
																							// predicate
																							// =
																							// no
																							// filtering

			if (testType != null) {
				spec = spec.and((root, query, cb) -> cb.equal(root.get("testType"), testType));
			}

			if (startDate != null) {
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("testDate"), startDate));
			}

			if (endDate != null) {
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("testDate"), endDate));
			}

			if (passed != null) {
				spec = spec.and((root, query, cb) -> cb.equal(root.get("passed"), passed));
			}

			List<VehicleTestResult> results = testResultRepository.findAll(spec);

			// *
			Specification<VehicleTestResult> spec2 = (root, query, criteriaBuilder) -> criteriaBuilder.and(
					criteriaBuilder.equal(root.get("testType"), testType),
					criteriaBuilder.equal(root.get("testDate"), startDate)); // */

			ResponseEntity.ok(results);
			ResponseEntity.accepted().body(results);
			new ResponseEntity<>(results, HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(results);
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
		}
	}

	public Specification<VehicleTestResult> createVehicleSpec(String key, Object value) {
		return (root, query, criteriaBuilder) -> {

			// 1. Create a list to hold your dynamic conditions
			List<Predicate> predicates = new ArrayList<>();

			// 2. Add conditions to the list based on your logic
			if (value != null) {
				predicates.add(criteriaBuilder.equal(root.get(key), value));
			}

			// You can add more conditions here
			// if (anotherVar != null) {
			// predicates.add(criteriaBuilder.like(root.get("someField"), "%" + anotherVar
			// + "%"));
			// }
			// 3. Combine the list of predicates with an AND
			// The toArray method converts the list into the varargs format required by
			// .and()
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
