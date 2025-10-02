/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VastDataSetsDemo {

	private final ExecutorService exeService = Executors.newFixedThreadPool(5);

	private static final List<String> VEH_MAKES = List.of("Ford", "Lincohn", "Buick");

	private static final List<String> VEH_MODELS = List.of("MODEL 1", "MODEL 2", "MODEL 3", "MODEL 4", "MODEL 5",
			"MODEL 6", "MODEL 7", "MODEL 8", "MODEL 9", "MODEL 10");

	private final Random RAND = new Random();

	private final int SIZE = 10;

	private List<Vehicle> vehicles = new ArrayList<>();

	public static void main(String[] args) {
		VastDataSetsDemo main = new VastDataSetsDemo();
		main.run();
		System.exit(0);
	}

	private void run() {
		List<Integer> ids = this.produceLargeDataIds();
		vehicles = this.produceVehicleDataByIds(ids);
		List<Vehicle> vehiclesFound = this.findByIds(ids);
		Map<String, List<Vehicle>> groupedVehicles = groupingVehiclesByMake(vehiclesFound);

		printVehicleinfo(groupedVehicles);
	}

	private void printVehicleinfo(Map<String, List<Vehicle>> groupedVehicles) {
		groupedVehicles.keySet().stream().forEach(k -> {
			log.info("  Make: {}", k);
			groupedVehicles.get(k).stream().forEach(v -> {
				log.info("          Model: {}", v.getModel());
			});
		});
	}

	public Map<String, List<Vehicle>> groupingVehiclesByMake(List<Vehicle> vehiclesFound) {
		return vehiclesFound.stream()
			.filter(v -> v.getId() != null)
			.sorted(Comparator.comparing(Vehicle::getMake).thenComparing(Vehicle::getModel))
			.collect(Collectors.groupingBy(Vehicle::getMake));
	}

	public List<Vehicle> findByIds(List<Integer> ids) {
		List<CompletableFuture<Vehicle>> futures = ids.stream()
			.map(vid -> CompletableFuture.supplyAsync(() -> findVehcileById(vid), exeService))
			.collect(Collectors.toList());

		return futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public Vehicle findVehcileById(Integer id) {
		return vehicles.stream()
			.filter(v -> Objects.equals(id, v.getId()))
			.findFirst()
			.orElse(Vehicle.builder().build());
	}

	@Data
	@Builder(toBuilder = true)
	public static class Vehicle {

		Integer id;

		String make;

		String model;

	}

	public List<Vehicle> produceVehicleDataByIds(List<Integer> records) {
		List<CompletableFuture<Vehicle>> futures = records.stream()
			.map(vid -> CompletableFuture.supplyAsync(() -> addVehcileById(vid), exeService))
			.collect(Collectors.toList());

		return futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public Vehicle addVehcileById(Integer id) {
		String make = VEH_MAKES.get(RAND.nextInt(VEH_MAKES.size()));
		String model = VEH_MODELS.get(RAND.nextInt(VEH_MODELS.size()));
		return Vehicle.builder().id(id).make(make).model(model).build();
	}

	public List<Integer> produceLargeDataIds() {
		List<Integer> list = new ArrayList<>(SIZE);
		for (int i = 0; i < SIZE; i++) {
			list.add(RAND.nextInt());
		}

		return list;
	}

}
