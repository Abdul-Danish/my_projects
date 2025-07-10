package com.dsa.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GeeksForGeeksProblems {
    
    public static void main(String args[]) {
        
        /*
         * Problem - 1: Missing in Array
         */
        
        /*
        // Input
        int[] arr = new int[] {8, 2, 4, 5, 3, 7, 1};
        int arrLength = arr.length;
        int missingNumber = 0;
        
        // TimeComplexity: O(n^2)
        // logic
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int i=1; i<=arrLength+1; i++) {
            boolean isPresent = false;
            for (int num : arr) {
                if (i == num) {
                    isPresent = true;
                    countMap.put(i, 1);
                }
            }
            if (!isPresent) {
                countMap.put(i, 0);
            }
        }
        for (Entry<Integer, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() == 0) {
                missingNumber = entry.getKey();
            }
        }
        System.out.println("Res: " + missingNumber);
        
        // TimeConplexity: O(n)
        // logic
        Set<Integer> set = new HashSet<>();
        for (int num : arr) {
            set.add(num);
        }
        
        for (int i=1; i<=arrLength+1; i++) {
            if (!set.contains(i)) {
                missingNumber = i;
            }
        }
        System.out.println("Res: " + missingNumber);
    */
    
        /*
         * Problem - 2: Index of an extra Element
         */
        
        /*
        // Input
        int[] a = new int[] {2,4,6,8,9,10,12};
        int[] b = new int[] {2,4,6,8,10,12};
                
        // Time Complexity: O(n)
        // logic
        List<Integer> bigList = new ArrayList<>();
        List<Integer> smallList = new ArrayList<>();
        
        if (a.length > b.length) {
            for (int aNum : a) {
                bigList.add(aNum);
            }
            for (int bNum : b) {
                smallList.add(bNum);
            }
        } else {            
            for (int aNum : a) {
                smallList.add(aNum);
            }
            for (int bNum : b) {
                bigList.add(bNum);
            }
        }
        
        int index = -1;
        for (int i=0; i<bigList.size(); i++) {
            if (!smallList.contains(bigList.get(i))) {
                index = i;
            }
        }
        System.out.println("index: " + index);
        */
        
        
        /*
         * Problem - 3: Second Largest
         */
        
        /*
        // Input
        int[] arr = new int[] {12, 35, 1, 10, 34, 1};
        
        // logic
        int highest = 0;
        int secondHighest = 0;
        for (int num : arr) {
            if (num > highest) {
                secondHighest = highest;
                highest = num;
            }
            
            if (highest > num && secondHighest < highest && num > secondHighest) {
                secondHighest = num;
            }
            
        }
        
        System.out.println("Res: " + secondHighest);
        */
    
        
        /*
         * Problem - 4: Array Duplicates
         */
        
        /*
        // Input
        int[] arr = new int[] {2, 3, 1, 2, 3};
        
        // logic
        Map<Integer, Integer> numberCount = new HashMap<>();
        List<Integer> result = new ArrayList<>();
        
        for (int num: arr) {
            numberCount.put(num, numberCount.getOrDefault(num, 0) + 1);
        }
        for (Entry<Integer, Integer> entry : numberCount.entrySet()) {
            if (entry.getValue() > 1) {
                result.add(entry.getKey());
            }
        }
        
        System.out.println("Res: " + result);
        */
        
        
        /*
         * Problem - 5: Minimum Jumps
         */
        
        /*
        int[] arr = new int[] {1, 3, 5, 8, 9, 2, 6, 7, 6, 8, 9};
        
        // logic
        int steps = 0;
        int noOfSteps = 0;
        int step = -1;
        int highest = 0;
        boolean isLengthExceeded = false;
        for (int i=0; i<arr.length; i++) {
            if (isLengthExceeded) {
                break;
            }
            highest = 0;
            if (step == -1) {
                step = arr[i];
                noOfSteps += step;
                continue;
            }
            for (int j=i; j<step+i; j++) {
                if (noOfSteps >= arr.length) {
                    isLengthExceeded = true;
                } else if (j < arr.length && highest < arr[j]) {
                    highest = arr[j];
                }
            }
            i = highest;
            step = highest;
            noOfSteps += step;
            steps++;
            System.out.println("i: " + i);
            System.out.println("Steps: " + steps);
        }
        
        System.out.println("Steps: " + steps);
        */
        
        /*
         * Problem - 6: 
         */
        
    }
    
}
