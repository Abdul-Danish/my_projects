package com.dsa.functional.interfsc;

public class FunctionalInterfaceImpl {

    public static void main(String[] args) {

        FuncInterface interface1;
        System.out.println("test: " + (interface1 = param -> " test22" + param).method(" method"));

        FuncInterface funcInterface = param1 -> param1 + " from lambda";
        System.out.println("func: " + add("Executing", funcInterface));
    }

    private static String add(String param, FuncInterface funcInterface) {
        return funcInterface.method(param);
    }

}
