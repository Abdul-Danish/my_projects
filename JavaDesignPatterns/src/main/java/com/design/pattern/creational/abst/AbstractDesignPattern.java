package com.design.pattern.creational.abst;

/*
 * Abstract Factory pattern is almost similar to Factory Pattern and is considered as another layer of abstraction over factory pattern.
 * 
 * Abstract Factory patterns work around a super-factory which creates other factories.
 */
public class AbstractDesignPattern {

    public static void main(String args[]) {

        NorthAmericaCarFactory northAmericaCarFactory = new NorthAmericaCarFactory();
        Client naClient = new Client(northAmericaCarFactory);
        Car naCar = naClient.getCar();
        naCar.assemble();
        CarSpecification naSpecification = naClient.getSpecification();
        naSpecification.dispay();

        System.out.println();

        EuropeCarFactory europeCarFactory = new EuropeCarFactory();
        Client euClient = new Client(europeCarFactory);
        Car euCar = euClient.getCar();
        euCar.assemble();
        CarSpecification euSpecification = euClient.getSpecification();
        euSpecification.dispay();
    }

}

class Client {

    private Car car;
    private CarSpecification carSpecification;

    public Client(CarFactory carFactory) {
        car = carFactory.createCar();
        carSpecification = carFactory.createSpecification();
    }

    public Car getCar() {
        return car;
    }

    public CarSpecification getSpecification() {
        return carSpecification;
    }
}

interface CarFactory {
    public Car createCar();

    public CarSpecification createSpecification();
}

class EuropeCarFactory implements CarFactory {

    @Override
    public Car createCar() {
        return new HatchBack();
    }

    @Override
    public CarSpecification createSpecification() {
        return new EuropeCarSpecification();
    }
}

class NorthAmericaCarFactory implements CarFactory {

    @Override
    public Car createCar() {
        return new Sedan();
    }

    @Override
    public CarSpecification createSpecification() {
        return new NorthAmericaCarSpecification();
    }
}

interface CarSpecification {
    void dispay();
}

class EuropeCarSpecification implements CarSpecification {

    @Override
    public void dispay() {
        System.out.println("Europe Car Specification: Fuel efficiency and emissions compliant with EU standards");
    }
}

class NorthAmericaCarSpecification implements CarSpecification {

    @Override
    public void dispay() {
        System.out.println("North America Car Specification: Safety features compliant with local regulations");
    }
}

interface Car {
    void assemble();
}

class HatchBack implements Car {

    @Override
    public void assemble() {
        System.out.println("Assembling HatcBack");
    }
}

class Sedan implements Car {

    @Override
    public void assemble() {
        System.out.println("Assembling Sedan");
    }
}
