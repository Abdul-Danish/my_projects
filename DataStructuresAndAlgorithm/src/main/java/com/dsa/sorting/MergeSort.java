package com.dsa.sorting;

public class MergeSort {

    public static void main(String[] args) {
        int[] arr = { 4, 1, 3, 9, 7 };
        divide(arr, 0, arr.length - 1);
        System.out.println("Sorted Array");
        for (int i : arr) {
            System.out.print(i + " ");
        }
    }

    private static void divide(int[] arr, int l, int r) {
        if (l < r) {
            int m = (l + r) / 2;
            divide(arr, l, m);
            divide(arr, m + 1, r);

            merge(arr, l, m, r);
        }
    }

    private static void merge(int[] arr, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        int[] L = new int[n1];
        int[] M = new int[n2];
        
        for (int i = 0; i< n1; i++) {
            L[i] = arr[l + i];
        }
        for (int j = 0; j< n2; j++) {
            M[j] = arr[j + m + 1];
        }
        
        int i = 0;
        int j = 0;
        int k = l;
        
        while (i < n1 && j < n2) {
            if (L[i] < M[j]) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = M[j];
                j++;
            }
            k++;
        }
        
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = M[j];
            j++;
            k++;
        }

    }

//    private static void merge(int[] arr, int l, int m, int r) {
//        int n1 = m - l + 1;
//        int n2 = r - m;
//
//        int[] L = new int[n1];
//        int[] M = new int[n2];
//
//        for (int i = 0; i < n1; i++) {
//            L[i] = arr[l + i];
//        }
//        for (int j = 0; j < n2; j++) {
//            M[j] = arr[m + 1 + j];
//        }
//
//        int i = 0;
//        int j = 0;
//        int k = l;
//
//        while (i < n1 && j < n2) {
//            if (L[i] < M[j]) {
//                arr[k] = L[i];
//                i++;
//            } else {
//                arr[k] = M[j];
//                j++;
//            }
//            k++;
//        }
//
//        if (i < n1) {
//            arr[k] = L[i];
//            i++;
//            k++;
//        }
//        if (j < n2) {
//            arr[k] = M[j];
//            j++;
//            k++;
//        }
//    }

}
