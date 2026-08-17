package com.dsa.sorting;

public class QuickSort {

    /* 
     *  select a pivot compare each item and swap in a way so that each item on left should be less than the pivot and items on 
     *  the right should be geater than the pivot.
     */
    
    // worst case: O(n^2), best case: (n * logn)
    public static void main(String[] args) {
        int[] arr = { 4, 1, 3, 9, 7 };
        quickSort(arr, 0, arr.length - 1);
        System.out.println("Sorted Array");
        for (int i : arr) {
            System.out.print(i + " ");
        }
    }
    
    private static void quickSort(int[] arr, int lowIndex, int highIndex) {
        if (lowIndex >= highIndex) {
            return;
        }

        int pivot = arr[highIndex];
        int leftPointer = lowIndex;
        int rightPointer = highIndex-1;
        while (leftPointer <= rightPointer) {
            // while increament / decrement the left / right pointer until it is less than / greater than pivot
            while (arr[leftPointer] <= pivot && leftPointer <= rightPointer) {
                leftPointer++;
            }
            while (arr[rightPointer] >= pivot && leftPointer <= rightPointer) {
                rightPointer--;
            }
            // if num is less than / greater than pivot swap
            if (leftPointer <= rightPointer) {
                swap(arr, leftPointer, rightPointer);
                leftPointer++;
                rightPointer--;
            }
        }
        // swap the pivot in between it's shorter and bigger number 
        swap(arr, leftPointer, highIndex);

        // recursively quickSort left and right side of the pivot
        quickSort(arr, lowIndex, leftPointer - 1);

        quickSort(arr, leftPointer + 1, highIndex);
    }

    
    private static void swap(int[] arr, int leftPointer, int rightPointer) {
        int temp = arr[leftPointer];
        arr[leftPointer] = arr[rightPointer];
        arr[rightPointer] = temp;
    }

}
