package com.design.pattern.structural.adapter;

/*
 * If you don't have access to the source code and wanted to use your own implementation, create an Adaptor for the type and use it
 * 
 * The adapter pattern convert the interface of a class into another interface clients expect.
 */
public class AdapterDesignPattern {

    public static void main(String[] args) {

        Assignment assignment = new Assignment();
        PenAdapter penAdapter = new PenAdapter();
        assignment.setPen(penAdapter);
        assignment.writeAssignment("Sample text");
    }
}
