package com.dsa.practice;

//@FunctionalInterface
public interface FunctInterface {

    // by default public abstract
    public abstract void method();
    
    // Cannot be overriden / used for utility methods
    static void method1() {
        System.out.println("Static method");
    }
    
    // default impl if not overriden
    default void method2() {
        
    }
}
