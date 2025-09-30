/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.codesignal;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author javaugi
 */
public class L11ThreadSafety {

    /*
  Performance Considerations
      synchronized has overhead; prefer lock-free alternatives when possible.
      Atomic* classes are faster for simple counters.
      ConcurrentHashMap offers segmented locking, allowing high concurrency.
      Minimize contention by limiting shared state or sharding data.
      Thread pool tuning matters — avoid too many threads for the CPU cores.
  🧪 Tools for Debugging and Testing
      Use FindBugs/SpotBugs, SonarQube, or IntelliJ Code Analysis
      Use JMH for benchmarking thread-safe classes
      Test with tools like jcstress, Thread Weaver, or Concurrency Test Harnesses
     */
    public static void main(String[] args) throws InterruptedException {
        L11ThreadSafety main = new L11ThreadSafety();
        main.runSafeCounters();
    }

    private void runSafeCounters() throws InterruptedException {
        SafeCounterAtomic counter = new SafeCounterAtomic(); // try other versions too

        Runnable task
                = () -> {
                    for (int i = 0; i < 100; i++) {
                        counter.increment();
                    }
                };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final count: " + counter.getCount());
    }

    public class SafeCounterAtomic {

        private AtomicInteger count = new AtomicInteger();

        public void increment() {
            count.incrementAndGet(); // atomic
        }

        public int getCount() {
            return count.get();
        }
    }

    public record UserRecord(Long id, String userName) {

    }

    // 1. Use Immutable Objects
    public final class User {

        private final String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // 2. Use Synchronized Blocks or Methods
    public class Counter {

        private int count = 0;

        // Race Condition Example (Broken Counter) will occur if the methods are not  synchronized
        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    // 3. Use java.util.concurrent Classes
    /*
     Prefer concurrent utilities over manual synchronization:
     1.    ConcurrentHashMap instead of HashMap
     2.    CopyOnWriteArrayList for read-heavy lists
     3.    AtomicInteger, AtomicReference for atomic updates
     */
    //  java.util.concurrent.atomic
    public class AtomicWay {

        HashMap<String, String> hashMap = new HashMap<>();
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap(hashMap);
        AtomicReference<HashMap<String, String>> atomicReference = new AtomicReference<>(hashMap);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        private final AtomicInteger atomicCount = new AtomicInteger();

        public void increment() {
            hashMap = atomicReference.get();
            Set<Entry<String, String>> set = concurrentHashMap.entrySet();
            atomicCount.incrementAndGet();
            list.stream().forEach(System.out::print);
        }
    }

    // 4. Use Thread-safe Collections
    //  java.util.concurrent
    public class ThreadSafeCollectionsWay {

        List<String> list = List.of("apple", "orange", "pear");
        List<String> synchronizedList = Collections.synchronizedList(list);
        // Collections.synchronizedList(new ArrayList<>());
        //  for simple thread-safe wrapping - This wrapper synchronizes all individual method calls like
        // .add(), .get(), .remove().
        // however Compound actions still need manual synchronization.

        // Or better: ConcurrentHashMap, ConcurrentLinkedQueue, etc.
        HashMap<String, String> hashMap = new HashMap<>();
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap(hashMap);
        // concurrentHashMap is thread-safe ✅
        // hashMap remains non-thread-safe

        AtomicReference<HashMap<String, String>> atomicReference = new AtomicReference<>(hashMap);
        CopyOnWriteArrayList<String> copyOnWriteList = new CopyOnWriteArrayList<>();

        public void run() {
            hashMap = atomicReference.get();
            Set<Entry<String, String>> set = concurrentHashMap.entrySet();
            synchronizedList.stream().forEach(System.out::print);
            copyOnWriteList.stream().forEach(System.out::print);

            synchronizedList.add("apple"); // safe
            synchronizedList.get(0); // safe

            synchronized (synchronizedList) {
                if (!synchronizedList.contains("apple")) {
                    synchronizedList.add("apple");
                }
            }

            synchronized (synchronizedList) {
                for (String item : synchronizedList) {
                    System.out.println(item);
                }
            }
        }
    }

    // 5. Avoid Shared Mutable State
    public class ThreadLocalWay {

        ThreadLocal<SimpleDateFormat> formatter
                = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    }

    // 6. Use ExecutorService Instead of Manual Threads
    public class ExcuterServiceWayInsteadOfManualThread {

        ExecutorService executor
                = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Callable<String> task = null;
        Future<String> future = executor.submit(task);
    }

    // 7. Proper Locking with ReentrantLock
    //  java.util.concurrent.locks
    public class ProperLockingWithReentrantLock {

        private int count = 0;
        private final Lock lock = new ReentrantLock();

        public void criticalSection() {
            lock.lock();
            try {
                count++;
                // critical section
            } finally {
                lock.unlock();
            }
        }

        public int getCount() {
            return count;
        }
    }

    // 8. Use Volatile for Visibility (Not for Atomicity)
    // Guarantees visibility: When one thread updates a volatile variable, the new value is
    // immediately visible to all other threads.
    // Prevents caching: Without volatile, a thread may cache a variable's value in a register or CPU
    // cache and not see the latest change made by another thread.
    public class WorkerThreadUseVolatileForVisibility extends Thread {

        private volatile boolean running = true;

        /* volatile ensures visibility between threads
       But it does not make compound actions (like increment) atomic
         */
        @Override
        public void run() {
            while (running) {
                // do work
            }

            stopWorker();
        }

        public boolean isRunning() {
            return running;
        }

        public void stopWorker() {
            running = false; // safely stops the thread
        }
    }
}
