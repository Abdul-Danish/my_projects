package com.dsa.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class Test {

    @Autowired
    private static classA a;

    public static void main(String[] args) {
        Test test = new Test();
        //
        List<String> list = new ArrayList<>();
        list.add("id1");
        list.stream().forEach(System.out::println);
        //

        //
        String str = "tst";
        Supplier<Integer> len = str::length;
        System.out.println(len.get());
        //

        //
        Predicate<String> predicate = new Predicate<String>() {
            @Override
            public boolean test(String t) {
                return t.equals("id1");
            }
        };

        List<String> collect = list.stream().filter(predicate).collect(Collectors.toList());
        for (String string : collect) {
            System.out.println(string);
        }
        //

//        a.test();

        List<Integer> lis = new ArrayList<>();
        
//        String binary = "110001011";
        String binary = "11111000000";

        boolean isBinary = split(binary, 0, true);
        System.out.println("is Binary: " + isBinary);
    }
    
    private static boolean split(String str, int len, boolean isValid) {

        int i_count = 0;
        int o_count = 0;
        while (len < str.length() && isValid) {
            for (int j = 0; j <= len; j++) {
                if ('1' == str.charAt(j)) {
                    i_count++;
                } else {
                    o_count++;
                }
            }

            System.out.println(isValid);
            if (i_count < o_count) {
                return false;
            }
            len++;
            isValid = split(str, len, isValid);
        }
        return isValid;
    }

    interface dummyInterface {
        void test();
    }

    @Component
    class classA implements dummyInterface {
        public void test() {
            System.out.println("Inside Class A");
        }
    }

    @Component
    class classB implements dummyInterface {
        public void test() {
            System.out.println("Inside Class B");
        }
    }
}
