package com.dsa.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.ToString;

public class Problems {

    public static void main(String[] args) {
        
        //  reverse a string
        
//        String str = "123";
//        String rev = "";
//        for (int i=str.length()-1; i>=0; i--) {
//            rev += str.charAt(i);
//        }
//        System.out.println("Rev String: " + rev);
        
        // swap two numbers without using a third variable

//        int a = 10;
//        int b = 20;
//        
//        a = a + b;
//        b = a - b;
//        a = a - b;
//        System.out.print("a: " + a + " b: " + b);
        
        
        // check if the given number is a prime number
        
//        int num = 47;
//        boolean isPrime = true;
//        
//        if (num == 1);
//        else if(num == 2 || num == 3);
//        else if (num % 2 == 0) isPrime = false;
//        else {
//            for (int i=3; i<=Math.sqrt(num); i+=2) {
////                System.out.print("i: " + i + " sqrt: " + Math.sqrt(num));
////                System.out.println();
//                if (num % i == 0) {
//                    isPrime = false;
//                    break;
//                }
//            }
//        }
//        System.out.println(isPrime);
        
        
        // creating a deadlock scenario programmatically
        
        // output:
//        Thread-2 acquiring lock on Problems.Thd2()
//        Thread-1 acquiring lock on Problems.Thd1()
//        Thread-2 acquired lock on Problems.Thd2()     // t-2 acquired Th2 obj
//        Thread-1 acquired lock on Problems.Thd1()     // t-1 acquired Th1 obj
//        Thread-1 acquiring lock on Problems.Thd2()    // t-1 waiting for Th2 obj acquired by t-2
//        Thread-2 acquiring lock on Problems.Thd1()    // t-2 waiting for Th1 obj acquired by t-1
        
//        Thd1 obj1 = new Thd1();
//        Thd2 obj2 = new Thd2();
//        
//        Thread t1 = new Thread(new SyncThread(obj1, obj2), "Thread-1");
//        Thread t2 = new Thread(new SyncThread(obj2, obj1), "Thread-2");
//        
//        t1.start();
//        t2.start();
        
        
        // factorial
        
//        int n = 10;
//        System.out.println(factorial(n));
        
        // Sort Map
        
//        Map<String, Integer> scores = new HashMap<>();
//        scores.put("David", 95);
//        scores.put("Jane", 80);
//        scores.put("Mary", 97);
//        scores.put("Lisa", 78);
//        scores.put("Dino", 65);
//        
//        Set<Entry<String, Integer>> entrySet = scores.entrySet();
//        List<Entry<String, Integer>> sortedList = new ArrayList<>(entrySet);
//        sortedList.sort((x,y) -> {
////            System.out.println(x + " " + y);
//            return x.getValue().compareTo(y.getValue());
//            }
//        );
//        System.out.println(sortedList);
        
        // Null as Argument
        
        foo(null);
        
    }
    
    public static void foo(Object o) {
        System.out.println("Object argument");
    }
    public static void foo(String s) {
        System.out.println("String argument");
    }
    
    
    public static int factorial(int n) {
        if (n==1) {
            return 1;
        }
        return n*factorial(n-1);
    }
    
    @ToString
    public static class Thd1 {
        
    }
    
    @ToString
    public static class Thd2 {
        
    }
    
    static class SyncThread implements Runnable  {
        
        private Object obj1;
        private Object obj2;
        
        public SyncThread(Object obj1, Object obj2) {
            this.obj1 = obj1;
            this.obj2 = obj2;
        }
        
        public void run() {
            String threadName = Thread.currentThread().getName();
            
            System.out.println(threadName + " acquiring lock on " + obj1);
            synchronized (obj1) {
                System.out.println(threadName + " acquired lock on " + obj1);
                work();
                System.out.println(threadName + " acquiring lock on " + obj2);
                synchronized (obj2) {
                    System.out.println(threadName + " acquired lock on " + obj2);
                    work();
                }
                System.out.println("lock released from " + obj2);
            }
            System.out.println("lock released from " + obj1);
            System.out.println("Completed...");   
        }
    
     private void work() { 
         try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
     }
     
        
    }

}
