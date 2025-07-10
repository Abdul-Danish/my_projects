package com.dsa.functional.interfsc;

import java.util.function.BiFunction;

public class MethodReferenceExample {

    public static void main(String[] args) {
        Demo myApp = new Demo();

        System.out.println("res1 (lambda expression): " + Demo.mergeStrings("Test ", "Concat!", (a, b) -> a + b));

        System.out.println("res2 (Reference to a static method): " + Demo.mergeStrings("Test ", "Concat!", Demo::concatStringStatic));

        System.out.println("res3 (Reference to an instance method of a particular object): "
            + Demo.mergeStrings("Test ", "Concat!", myApp::concatStrings));

        System.out.println("res4 (Reference to an instance method of an arbitrary object of particular type): "
            + Demo.mergeStrings("Test ", "Concat!", String::concat));
    }

}

class Demo {

    public static String mergeStrings(String str1, String str2, BiFunction<String, String, String> biFunction) {
        return biFunction.apply(str1, str2);
    }

    public static String concatStringStatic(String str1, String str2) {
        return str1 + str2;
    }

    public String concatStrings(String str1, String str2) {
        return str1 + str2;
    }
}
