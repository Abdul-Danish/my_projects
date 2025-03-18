package com.design.pattern.structural.bridge;

/*
 * Allows the Abstraction and the Implementation to be developed independently.
 * 
 * Bridge pattern decouple an abstraction from its implementation so that the two can vary independently. 
 * 
 * Similar To: Template Design Pattern
 */
public class BridgeDesignPattern {

    public static void main(String args[]) {
        Vechicle car = new Car(new Produce(), new Assemble());
        car.manufacture();

        System.out.println();

        Vechicle bike = new Bike(new Produce(), new Assemble());
        bike.manufacture();
    }
}

abstract class Vechicle {
    protected Workshop workshop1;
    protected Workshop workshop2;

    public Vechicle(Workshop workshop1, Workshop workshop2) {
        this.workshop1 = workshop1;
        this.workshop2 = workshop2;
    }

    abstract void manufacture();
}

class Car extends Vechicle {

    public Car(Workshop workshop1, Workshop workshop2) {
        super(workshop1, workshop2);
    }

    @Override
    public void manufacture() {
        workshop1.work("Car");
        workshop2.work("Car");
    }
}

class Bike extends Vechicle {

    public Bike(Workshop workshop1, Workshop workshop2) {
        super(workshop1, workshop2);
    }

    @Override
    public void manufacture() {
        workshop1.work("Bike");
        workshop2.work("Bike");
    }
}

interface Workshop {
    void work(String type);
}

class Produce implements Workshop {

    @Override
    public void work(String type) {
        System.out.println(type + " produced");
    }
}

class Assemble implements Workshop {

    @Override
    public void work(String type) {
        System.out.println(type + " Assembled");
    }
}
