/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.hackerrank.cvshealth;

import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrescriptionQueue {

	// Represents a single prescription order
	public static class PrescriptionOrder {

		private final String orderId;

		private final String patientId;

		private final boolean isStat; // STAT orders are highest priority

		private final long fillByDate; // Unix timestamp in milliseconds

		public PrescriptionOrder(String orderId, String patientId, boolean isStat, long fillByDate) {
			this.orderId = Objects.requireNonNull(orderId);
			this.patientId = Objects.requireNonNull(patientId);
			this.isStat = isStat;
			this.fillByDate = fillByDate;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getPatientId() {
			return patientId;
		}

		public boolean isStat() {
			return isStat;
		}

		public long getFillByDate() {
			return fillByDate;
		}

		@Override
		public String toString() {
			return "PrescriptionOrder{" + "orderId='" + orderId + '\'' + ", patientId='" + patientId + '\''
					+ ", isStat=" + isStat + ", fillByDate=" + fillByDate + '}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			PrescriptionOrder that = (PrescriptionOrder) o;
			return isStat == that.isStat && fillByDate == that.fillByDate && orderId.equals(that.orderId)
					&& patientId.equals(that.patientId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(orderId, patientId, isStat, fillByDate);
		}

	}

	// Custom Comparator for PrescriptionOrder to define priority
	private static class OrderComparator implements Comparator<PrescriptionOrder> {

		@Override
		public int compare(PrescriptionOrder o1, PrescriptionOrder o2) {
			// STAT orders first
			if (o1.isStat() && !o2.isStat()) {
				return -1;
			}
			if (!o1.isStat() && o2.isStat()) {
				return 1;
			}
			// If both STAT or both not STAT, compare by fillByDate (earliest first)
			int dateComparison = Long.compare(o1.getFillByDate(), o2.getFillByDate());
			if (dateComparison != 0) {
				return dateComparison;
			}
			// If fillByDate is the same, compare by orderId (lowest ID first)
			return o1.getOrderId().compareTo(o2.getOrderId());
		}

	}

	private final PriorityQueue<PrescriptionOrder> queue;

	private final Lock lock = new ReentrantLock(); // For thread safety

	public PrescriptionQueue() {
		this.queue = new PriorityQueue<>(new OrderComparator());
	}

	/**
	 * Adds a new prescription order to the queue. This method is thread-safe.
	 * @param order The PrescriptionOrder to add.
	 */
	public void addOrder(PrescriptionOrder order) {
		lock.lock();
		try {
			queue.offer(order);
			System.out.println(Thread.currentThread().getName() + " added: " + order.getOrderId());
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * Retrieves and removes the highest priority prescription order from the queue. This
	 * method is thread-safe.
	 * @return The highest priority PrescriptionOrder, or null if the queue is empty.
	 */
	public PrescriptionOrder getNextOrder() {
		lock.lock();
		try {
			PrescriptionOrder nextOrder = queue.poll();
			if (nextOrder != null) {
				System.out.println(Thread.currentThread().getName() + " retrieved: " + nextOrder.getOrderId());
			}
			else {
				System.out.println(Thread.currentThread().getName() + " queue is empty.");
			}
			return nextOrder;
		}
		finally {
			lock.unlock();
		}
	}

	/**
	 * Returns the number of orders currently in the queue.
	 * @return The size of the queue.
	 */
	public int size() {
		lock.lock();
		try {
			return queue.size();
		}
		finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		PrescriptionQueue pq = new PrescriptionQueue();

		// Simulate orders being added by multiple threads
		Thread producer1 = new Thread(() -> {
			pq.addOrder(new PrescriptionOrder("P001", "PatA", false, System.currentTimeMillis() + 5000));
			pq.addOrder(new PrescriptionOrder("P003", "PatC", true, System.currentTimeMillis() + 1000)); // STAT
			pq.addOrder(new PrescriptionOrder("P005", "PatE", false, System.currentTimeMillis() + 2000));
		}, "Producer-1");

		Thread producer2 = new Thread(() -> {
			pq.addOrder(new PrescriptionOrder("P002", "PatB", false, System.currentTimeMillis() + 10000));
			pq.addOrder(new PrescriptionOrder("P004", "PatD", true, System.currentTimeMillis() + 500)); // STAT
			pq.addOrder(new PrescriptionOrder("P006", "PatF", false, System.currentTimeMillis() + 2000)); // Same
																											// fill
																											// date
																											// as
																											// P005
		}, "Producer-2");

		producer1.start();
		producer2.start();

		producer1.join();
		producer2.join();

		System.out.println("\n--- Processing Orders ---");

		// Simulate orders being processed by multiple threads
		Runnable consumerTask = () -> {
			for (int i = 0; i < 3; i++) { // Each consumer tries to get 3 orders
				PrescriptionOrder order = pq.getNextOrder();
				if (order != null) {
					try {
						Thread.sleep(500); // Simulate processing time
					}
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				else {
					break;
				}
			}
		};

		Thread consumer1 = new Thread(consumerTask, "Consumer-1");
		Thread consumer2 = new Thread(consumerTask, "Consumer-2");

		consumer1.start();
		consumer2.start();

		consumer1.join();
		consumer2.join();

		System.out.println("\nRemaining orders in queue: " + pq.size());
	}

}
