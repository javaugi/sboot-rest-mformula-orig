/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.hrank;

/**
 * Example of a thread-safe singleton class using the Initialization-on-demand holder
 * idiom (Bill Pugh Singleton). This approach is thread-safe without explicit
 * synchronization.
 */
public class ThreadSafeSingleton {

	// Private constructor to prevent direct instantiation
	private ThreadSafeSingleton() {
		// Optional: Add logic here for initialization if needed
		System.out.println("Singleton instance is being created.");
	}

	/**
	 * Inner static class that holds the singleton instance. This class is not loaded
	 * until getInstance() is called for the first time, ensuring lazy initialization.
	 */
	private static class SingletonHolder {

		// The single instance of the singleton class.
		// This is initialized when the SingletonHolder class is loaded.

		private static final ThreadSafeSingleton INSTANCE = new ThreadSafeSingleton();

	}

	/**
	 * Public static method to get the single instance of the singleton class. This is the
	 * only way to access the singleton instance.
	 * @return The single instance of ThreadSafeSingleton.
	 */
	public static ThreadSafeSingleton getInstance() {
		// When getInstance() is called for the first time, the SingletonHolder class
		// is loaded, and the INSTANCE is initialized. Subsequent calls return
		// the already initialized instance.
		return SingletonHolder.INSTANCE;
	}

	private static ThreadSafeSingleton singleton = null;

	public static ThreadSafeSingleton getInstance2() {
		// When getInstance() is called for the first time, the SingletonHolder class
		// is loaded, and the INSTANCE is initialized. Subsequent calls return
		// the already initialized instance.
		if (singleton == null) {
			singleton = new ThreadSafeSingleton();
		}
		return singleton;
	}

	// Example method to demonstrate the singleton's usage
	public void showMessage() {
		System.out.println("Hello from the thread-safe singleton instance!");
	}

	// You can add other methods and fields as needed for your singleton's functionality.
	// Example of how to use the singleton:
	// public static void main(String[] args) {
	// ThreadSafeSingleton singleton1 = ThreadSafeSingleton.getInstance();
	// singleton1.showMessage();
	//
	// ThreadSafeSingleton singleton2 = ThreadSafeSingleton.getInstance();
	// singleton2.showMessage();
	//
	// // Verify that both references point to the same instance
	// System.out.println("Are singleton1 and singleton2 the same instance? " +
	// (singleton1 ==
	// singleton2));
	// }

}
