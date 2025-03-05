package com.design.pattern.structural.bridge;

/*
 * Bridge pattern decouple an abstraction from its implementation so that the two can vary independently. 
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
        System.out.println("Car");
        workshop1.work();
        workshop2.work();
    }
}

class Bike extends Vechicle {

    public Bike(Workshop workshop1, Workshop workshop2) {
        super(workshop1, workshop2);
    }

    @Override
    public void manufacture() {
        System.out.println("Bike");
        workshop1.work();
        workshop2.work();
    }
}

interface Workshop {
    void work();
}

class Produce implements Workshop {

    @Override
    public void work() {
        System.out.println("produced");
    }
}

class Assemble implements Workshop {

    @Override
    public void work() {
        System.out.println("Assembled");
    }
}
