package com.design.pattern.creational.singleton;

import java.util.Objects;

/*
 * It guarantees that a class has just one instance and offers a way to access it globally.
 * 
 * In multi-threaded environments, the process of creating and initializing a Singleton can lead to race conditions if multiple threads try to create it simultaneously.
 */
public class SingletonDesignPattern {

    public static void main(String args[]) {
        Singleton instance1 = Singleton.getInstance();
        instance1.exec();
        instance1.setDummy("tst");
        System.out.println(instance1.getDummy());
        Singleton instance2 = Singleton.getInstance();
        instance2.exec();
        System.out.println(instance2.getDummy());
    }
}

class Singleton {

    private static Singleton obj;
    private String dummy;

    private Singleton() {
        System.out.println("Singleton Instance Created");
    }

    public static synchronized Singleton getInstance() {
        if (Objects.isNull(obj)) {
            System.out.println("Creating New Singleton Instance");
            obj = new Singleton();
        }
        System.out.println("Singleton Instance already exists");
        return obj;
    }

    public void exec() {
        System.out.println("Method Executed");
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    public String getDummy() {
        return dummy;
    }
}