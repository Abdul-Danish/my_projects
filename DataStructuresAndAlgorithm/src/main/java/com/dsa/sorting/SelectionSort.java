package com.dsa.sorting;

public class SelectionSort {

    // find the minimum value in each iteration and swap it
    // worst case: O(n^2), best case: (n^2)
    public static void main(String[] args) {
        int[] arr = { 4, 1, 3, 9, 7 };
        selectionSort(arr);
        System.out.println("Sorted Array");
        for (int i : arr) {
            System.out.print(i + " ");
        }
    }

    private static void selectionSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
}
