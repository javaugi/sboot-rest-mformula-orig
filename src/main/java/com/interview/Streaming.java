/*
 * Copyright (C) 2019 Strategic Information Systems, LLC.
 *
 */
package com.interview;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author javaugi
 * @version $LastChangedRevision $LastChangedDate Last Modified Author: $LastChangedBy
 */
/*
 * What is Stream? Stream represents a sequence of objects from a source, which supports
 * aggregate operations. Following are the characteristics of a Stream 1 Sequence of
 * elements − A stream provides a set of elements of specific type in a sequential manner.
 * A stream gets/computes elements on demand. It never stores the elements. 2. Source −
 * Stream takes Collections, Arrays, or I/O resources as input source. 3. Aggregate
 * operations − Stream supports aggregate operations like filter, map, limit, reduce,
 * find, match, and so on. 4. Pipelining − Most of the stream operations return stream
 * itself so that their result can be pipelined. These operations are called intermediate
 * operations and their function is to take input, process them, and return output to the
 * target. collect() method is a terminal operation which is normally present at the end
 * of the pipelining operation to mark the end of the stream. 5. Automatic iterations −
 * Stream operations do the iterations internally over the source elements provided, in
 * contrast to Collections where explicit iteration is required.
 */
@Slf4j
public class Streaming {

    public static void main(String[] args) {
		Streaming m = new Streaming();
		int[] points = { 10, 5, 20, 15, 30, 25 }; // Example customer points
		m.processPoints(points);

		Arrays.sort(points);
		log.debug("Second largest=" + points[points.length - 2]);
		List<Integer> list = Arrays.stream(points).boxed().sorted().collect(Collectors.toList());
		log.debug("Second largest=" + list.get(points.length - 2));

		List<String> inputs = Arrays.asList("{}()", "({()})", "{}(", "[])");
		log.info("isBalancedParentheses ...");
		inputs.stream().forEach(s -> log.debug((isBalancedParentheses(s) ? "true" : "false")));
		log.info("isBalancedParentheses Done");

		List<String> myList = Arrays.asList("a1", "a2", "b1", "c2", "c1");
		log.info("Printing {}", myList);

		log.info("EX1 ", myList);
		myList.stream().filter(s -> s.startsWith("c")).map(String::toUpperCase).sorted().forEach(System.out::println);

		log.info("EX2 ");
		Stream.of("d2", "a2", "b1", "b3", "c").filter(s -> {
			log.debug("filter: " + s);
			return true;
		}).forEach(s -> log.debug("forEach: " + s));

		log.info("Ex3 ");
		Stream.of("d2", "a2", "b1", "b3", "c").map(s -> {
			log.debug("map: " + s);
			return s.toUpperCase();
		}).anyMatch(s -> {
			log.debug("anyMatch: " + s);
			return s.startsWith("A");
		});
		log.info("Parallel ");
		parallelStream();

		log.info("Sorted \n ");
		Stream.of("d2", "a2", "b1", "b3", "c").sorted((s1, s2) -> {
			System.out.printf("sort: %s; %s\n", s1, s2);
			return s1.compareTo(s2);
		}).filter(s -> {
			log.debug("filter: " + s);
			return s.startsWith("a");
		}).map(s -> {
			log.debug("map: " + s);
			return s.toUpperCase();
		}).forEach(s -> log.debug("forEach: " + s));

		log.info("persons \n ");
		persons();

		log.info("parallelStreams \n ");
		parallelStreams();
	}

	public static boolean isBalancedParentheses(String s) {
		Deque<Character> stack = new ArrayDeque<>();

		for (char c : s.toCharArray()) {
			if (c == '{' || c == '[' || c == '(') {
				stack.push(c);
			}
			else {
				if (stack.isEmpty()) {
					return false;
				}

				char top = stack.pop();
				if ((c == '}' && top != '{') || (c == ']' && top != '[') || (c == ')' && top != '(')) {
					return false;
				}
			}
		}

		return stack.isEmpty();
	}

	public static Integer sum(List<Integer> ar) {
		return ar.stream().mapToInt(Integer::intValue).sum();
	}

	class PointsVO implements Comparable<PointsVO> {

		int index;

		int point;

		int total;

		public PointsVO(int index, int point) {
			this.index = index;
			this.point = point;
			this.total = point;
		}

		public int getIndex() {
			return index;
		}

		public int getPoint() {
			return point;
		}

		public int getTotal() {
			return total;
		}

		public void addToTotal(int points) {
			this.total += points;
		}

		@Override
		public int compareTo(PointsVO o) {
			return Integer.valueOf(this.getTotal()).compareTo(o.getTotal());
		}

		@Override
		public String toString() {
			return "\n\t ** PointsVO{" + "index=" + index + ", point=" + point + ", total=" + total + '}';
		}

	}

	private void printTop3Reward(List<PointsVO> vos) {
		final int s = vos.size();
		log.info("1 printTop3Reward Initial list size {} {}", s, vos);
		vos.stream().forEach(vo -> vo.addToTotal(s));
		log.info("2 printTop3Reward with total {}", vos);

		vos = vos.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
		log.info("3 printTop3Reward reverse sorted {}", vos);
		for (int i = 0; i < 3; i++) {
			log.info("Ranked Number {} {}", (i + 1), vos.get(i));
		}
	}

	private void processPoints(int[] points) {
		if (points == null) {
			log.info("Empty points array");
			return;
		}
		if (points.length < 3) {
			log.info("Three array elements required {}", Arrays.toString(points));
			return;
		}

		log.info("1 processPoints Initial array {}", Arrays.toString(points));
		List<PointsVO> vos = new ArrayList<>();
		for (int i = 0; i < points.length; i++) {
			vos.add(new PointsVO(i, points[i]));
		}
		printTop3Reward(vos);
	}

	public static void parallelStream() {
		Arrays.asList("a1", "a2", "b1", "c2", "c1").parallelStream().filter(s -> {
			System.out.format("filter: %s [%s]\n", s, Thread.currentThread().getName());
			return true;
		}).map(s -> {
			System.out.format("map: %s [%s]\n", s, Thread.currentThread().getName());
			return s.toUpperCase();
		}).forEach(s -> System.out.format("forEach: %s [%s]\n", s, Thread.currentThread().getName()));
	}

	@Data
	static class Person {

		String name;

		int age;

		Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	static List<Person> persons = Arrays.asList(new Person("Max", 18), new Person("Peter", 23),
			new Person("Pamela", 23), new Person("David", 12));

	private static void persons() {
		List<Person> filtered = persons.stream().filter(p -> p.name.startsWith("P")).collect(Collectors.toList());

		log.debug("" + filtered); // [Peter, Pamela]

		Map<Integer, List<Person>> personsByAge = persons.stream().collect(Collectors.groupingBy(p -> p.age));

		personsByAge.forEach((age, p) -> System.out.format("age %s: %s\n", age, p));
		// age 18: [Max]
		// age 23: [Peter, Pamela]
		// age 12: [David]

		Collector<Person, StringJoiner, String> personNameCollector = Collector.of(() -> new StringJoiner(" | "), // supplier
				(j, p) -> j.add(p.name.toUpperCase()), // accumulator
				(j1, j2) -> j1.merge(j2), // combiner
				StringJoiner::toString); // finisher

		String names = persons.stream().collect(personNameCollector);

		log.debug(names); // MAX | PETER | PAMELA | DAVID
	}

	private static void parallelStreams() {
		ForkJoinPool commonPool = ForkJoinPool.commonPool();
		log.debug("" + commonPool.getParallelism()); // 3

		Arrays.asList("a1", "a2", "b1", "c2", "c1").parallelStream().filter(s -> {
			System.out.format("filter: %s [%s]\n", s, Thread.currentThread().getName());
			return true;
		}).map(s -> {
			System.out.format("map: %s [%s]\n", s, Thread.currentThread().getName());
			return s.toUpperCase();
		}).forEach(s -> System.out.format("forEach: %s [%s]\n", s, Thread.currentThread().getName()));

		persons.parallelStream().reduce(0, (sum, p) -> {
			System.out.format("accumulator: sum=%s; person=%s [%s]\n", sum, p, Thread.currentThread().getName());
			return sum += p.age;
		}, (sum1, sum2) -> {
			System.out.format("combiner: sum1=%s; sum2=%s [%s]\n", sum1, sum2, Thread.currentThread().getName());
			return sum1 + sum2;
		});
	}

	private void sortPeople() {
		Collections.sort(persons, new NameComparator()); // Uses custom ordering

		// Sort by name
		Comparator<Person> byName = (p1, p2) -> p1.getName().compareTo(p2.getName());

		// Sort by age
		Comparator<Person> byAge = Comparator.comparingInt(Person::getAge);
		Comparator<Person> byAge2 = Comparator.comparing(Person::getAge);

		// Sort by age then name
		Comparator<Person> byAgeThenName = Comparator.comparingInt(Person::getAge).thenComparing(Person::getName);

		// Sort by age then name
		Comparator<Person> byAgeThenName2 = Comparator.comparing(Person::getAge).thenComparing(Person::getName);

		persons.sort(byAgeThenName);
	}

	/*
	 * Key Points: .reversed() reverses the entire preceding comparator chain
	 * Comparator.reverseOrder() is used for specific field reverse sorting Order matters
	 * - the first comparator has highest priority Use thenComparing() with a custom
	 * comparator for individual field reverse sorting
	 */
	public void sortPeopleReverse() {
		// 1. Reverse Both Age and Name
		Comparator<Person> reverseAgeThenReverseName = Comparator.comparingInt(Person::getAge)
			.reversed()
			.thenComparing(Person::getName)
			.reversed();

		persons.sort(reverseAgeThenReverseName);

		// 2. Using Separate Reverse Comparators
		Comparator<Person> reverseAgeThenReverseName2 = Comparator.comparingInt(Person::getAge)
			.reversed()
			.thenComparing(Comparator.comparing(Person::getName).reversed());
		persons.sort(reverseAgeThenReverseName2);

		// 3. Using Method References with Negative Comparison
		Comparator<Person> reverseAgeThenReverseName3 = Comparator.comparingInt((Person p) -> -p.getAge()) // Reverse
																											// age
			.thenComparing(p -> p.getName(), Comparator.reverseOrder()); // Reverse name
		persons.sort(reverseAgeThenReverseName3);

		// 4. Most Readable Approach
		Comparator<Person> reverseAgeThenReverseName4 = Comparator.comparingInt(Person::getAge)
			.reversed()
			.thenComparing(Person::getName, Comparator.reverseOrder());
		persons.sort(reverseAgeThenReverseName4);

		// 6. Alternative Using Collections.sort()
		// Sort in place
		persons.sort(Comparator.comparingInt(Person::getAge)
			.reversed()
			.thenComparing(Person::getName, Comparator.reverseOrder()));

		// 7. Multiple Reverse Combinations
		// Age descending, name ascending
		Comparator<Person> reverseAgeThenName = Comparator.comparingInt(Person::getAge)
			.reversed()
			.thenComparing(Person::getName);
		persons.sort(reverseAgeThenName);

		// Age ascending, name descending
		Comparator<Person> ageThenReverseName = Comparator.comparingInt(Person::getAge)
			.thenComparing(Person::getName, Comparator.reverseOrder());
		persons.sort(ageThenReverseName);

		// Both descending (what you asked for)
		Comparator<Person> bothReverse = Comparator.comparingInt(Person::getAge)
			.reversed()
			.thenComparing(Person::getName, Comparator.reverseOrder());
		persons.sort(bothReverse);

		// 8. Using Static Import for Readability
		List<Person> sorted = persons.stream()
			.sorted(comparingInt(Person::getAge).reversed().thenComparing(Person::getName, reverseOrder()))
			.collect(Collectors.toList());
	}

	class NameComparator implements Comparator<Person> {

		@Override
		public int compare(Person p1, Person p2) {
			return p1.name.compareTo(p2.name);
		}

	}

}
