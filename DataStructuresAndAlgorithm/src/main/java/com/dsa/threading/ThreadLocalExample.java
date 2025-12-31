package com.dsa.threading;

import java.util.function.Supplier;

/*
 * thread-local variable is a variable that is local to a specific thread.
 * 
 * Each thread accessing the thread-local variable gets its own independent copy, isolated from other threads.
 */
public class ThreadLocalExample {

    private static ThreadLocal<Integer> threadLocal1;
    private static ThreadLocal<Integer> threadLocal2;

    public static void main(String[] args) {
        //
        threadLocal1 = new ThreadLocal<>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };

        // Note: can use either just different initializations threadLocal1 / threadLocal2.

        Supplier<Integer> supplier = () -> 0;
        threadLocal2 = ThreadLocal.withInitial(supplier);
        //

        Runnable runnable = () -> {
            threadLocal2.set((int) (Math.random() * 100));
            System.out.println("Thread name: " + Thread.currentThread().getName() + " value: " + threadLocal2.get());
        };

        new Thread(runnable, "Thread 1").start();
        new Thread(runnable, "Thread 2").start();
        new Thread(runnable, "Thread 3").start();
    }

}
