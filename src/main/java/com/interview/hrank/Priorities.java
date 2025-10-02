/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author javaugi
 */
public class Priorities {

	public List<Student> getStudents(List<String> events) {

		/*
		 * Student Class: Stores student information (ID, name, CGPA) Provides getter
		 * methods for accessing these properties
		 * 
		 * StudentComparator:
		 * 
		 * Implements the priority rules: Higher CGPA students come first For same CGPA,
		 * alphabetical name order For same CGPA and name, lower ID comes first
		 */
		PriorityQueue<Student> queue = new PriorityQueue<>(new StudentComparator());

		for (String event : events) {
			String[] parts = event.split(" ");
			if (parts[0].equals("ENTER")) {
				String name = parts[1];
				double cgpa = Double.parseDouble(parts[2]);
				int id = Integer.parseInt(parts[3]);
				queue.add(new Student(id, name, cgpa));
			}
			else if (parts[0].equals("SERVED")) {
				if (!queue.isEmpty()) {
					queue.poll();
				}
			}
		}

		/*
		 * events.stream().filter(event -> { String[] parts = event.split(" "); if
		 * (parts[0].equals("ENTER")) { String name = parts[1]; double cgpa =
		 * Double.parseDouble(parts[2]); int id = Integer.parseInt(parts[3]);
		 * queue.add(new Student(id, name, cgpa)); } else if (parts[0].equals("SERVED")) {
		 * if (!queue.isEmpty()) { queue.poll(); } } return true;
		 * }).collect(Collectors.toList()); //
		 */
		List<Student> remainingStudents = new ArrayList<>(queue);

		Comparator<Student> studentComparator = new Comparator<Student>() {
			@Override
			public int compare(Student s1, Student s2) {
				if (s1.getCGPA() != s2.getCGPA()) {
					return Double.valueOf(s1.getCGPA()).compareTo(s2.getCGPA());
				}
				if (!s1.getName().equals(s2.getName())) {
					return s1.getName().compareTo(s2.getName());
				}
				return Integer.valueOf(s1.getID()).compareTo(s2.getID());
			}
		};

		remainingStudents.sort(studentComparator);
		return remainingStudents;
	}

}
