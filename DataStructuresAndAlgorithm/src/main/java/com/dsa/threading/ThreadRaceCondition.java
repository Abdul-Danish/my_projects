package com.dsa.threading;

public class ThreadRaceCondition {

    // Without thread safety (Race condition):
    static class Counter {

        int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String args[]) {

        Counter counter = new Counter();
        Runnable task = () -> {
            for (int i = 0; i < 5000; i++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread Execution Failed");
        }

        System.out.println("Count: " + counter.getCount());
    }

}
