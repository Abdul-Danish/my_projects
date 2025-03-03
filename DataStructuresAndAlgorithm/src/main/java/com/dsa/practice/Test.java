package com.dsa.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Test {

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
    }

}
