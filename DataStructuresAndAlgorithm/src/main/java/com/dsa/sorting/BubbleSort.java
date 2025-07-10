package com.dsa.sorting;

public class BubbleSort {

	public static void main(String[] args) {
	    int[] arr = { 4, 1, 3, 9, 7 };
        int[] bubbleSortRes = bubbleSort(arr, arr.length);
        System.out.println("Sorted Array");
        for (int i : bubbleSortRes) {
            System.out.print(i + " ");
        }
	}
	
	private static int[] bubbleSort(int[] arr, int length) {
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (arr[j] < arr[i]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }

}
