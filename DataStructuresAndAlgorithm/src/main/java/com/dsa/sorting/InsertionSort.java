package com.dsa.sorting;

public class InsertionSort {
    
    public static void main(String[] args) {
        int[] arr = { 4, 1, 3, 9, 7 };
        insertionSort(arr);
        System.out.println("Sorted Array");
        for (int i : arr) {
            System.out.print(i + " ");
        }
    }
    
    private static void insertionSort(int[] arr) {
        for (int step=1; step<arr.length; step++) {
            int key = arr[step];
            int j = step-1;
            
            while(j >= 0 && key < arr[j]) {
                swap(arr, j, step);
                j--;
            }
        }
    }
    
    private static void swap(int[] arr, int leftPointer, int rightPointer) {
        int temp = arr[leftPointer];
        arr[leftPointer] = arr[rightPointer];
        arr[rightPointer] = temp;
    }

}
