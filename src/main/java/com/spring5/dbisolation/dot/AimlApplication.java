/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation.dot;

import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

// @SpringBootApplication
public class AimlApplication {

	public static void main(String[] args) {
		SpringApplication.run(AimlApplication.class, args);
	}

	/**
	 * This method is a CommandLineRunner that will run on application startup. It's used
	 * here to pre-populate the H2 in-memory database with some sample tasks. This is a
	 * great way to showcase Spring's dependency injection and get some data for the
	 * frontend to display immediately.
	 */
	@Bean
	CommandLineRunner initDatabase(TaskRepository repository) {
		return args -> {
			// Sample data using Java 8's LocalDate
			Task task1 = new Task();
			task1.setTitle("Complete Interview Guide");
			task1.setDescription("Write the full-stack interview guide and code examples.");
			task1.setCompleted(false);
			task1.setDueDate(LocalDate.of(2025, 8, 5));
			repository.save(task1);

			Task task2 = new Task();
			task2.setTitle("Prepare for Gemini Interview");
			task2.setDescription("Review all topics and code before the interview.");
			task2.setCompleted(true);
			task2.setDueDate(LocalDate.of(2025, 7, 30));
			repository.save(task2);
		};
	}

}
