package com.design.pattern.creational.abst;

/*
 * Abstract Factory pattern is almost similar to Factory Pattern and is considered as another 
 * layer of abstraction over factory pattern.
 * 
 * Abstract Factory patterns work around a super-factory which creates other factories.
 * 
 * It's an abstraction on top of Factories to create Factory.
 * 
 * Similar To: Factory Pattern
 */
public class AbstractDesignPattern {

    public static void main(String args[]) {

        // Creating multiple objects from Factory
        NorthAmericaVechicleFactory northAmericaCarFactory = new NorthAmericaVechicleFactory();
        Vechicle car = northAmericaCarFactory.createCar();
        car.assemble();
        Vechicle bike = northAmericaCarFactory.createBike();
        bike.assemble();

        /*
         * Using Client
         * 
         * NorthAmericaVechicleFactory northAmericaCarFactory = new NorthAmericaVechicleFactory(); Client naClient = new
         * Client(northAmericaCarFactory); Vechicle naCar = naClient.getCar(); naCar.assemble(); VechicleSpecification naSpecification =
         * naClient.getSpecification(); naSpecification.dispay();
         * 
         * System.out.println();
         * 
         * EuropeVechicleFactory europeCarFactory = new EuropeVechicleFactory(); Client euClient = new Client(europeCarFactory); Vechicle
         * euCar = euClient.getCar(); euCar.assemble(); VechicleSpecification euSpecification = euClient.getSpecification();
         * euSpecification.dispay(); }
         */
    }
}

    class Client {
        private VechicleFactory vechicleFactory;
        private VechicleSpecification vechicleSpecification;

        public Client(VechicleFactory vechicleFactory) {
            this.vechicleFactory = vechicleFactory;
            vechicleSpecification = vechicleFactory.createSpecification();
        }

        public Vechicle getCar() {
            return vechicleFactory.createCar();
        }

        public Vechicle getBike() {
            return vechicleFactory.createBike();
        }

        public VechicleSpecification getSpecification() {
            return vechicleSpecification;
        }
    }

    interface VechicleFactory {
        Vechicle createCar();

        Vechicle createBike();

        public VechicleSpecification createSpecification();
    }

    class EuropeVechicleFactory implements VechicleFactory {

        @Override
        public Vechicle createCar() {
            return new HatchBack();
        }

        @Override
        public Vechicle createBike() {
            return new BianchiBike();
        }

        @Override
        public VechicleSpecification createSpecification() {
            return new EuropeVechicleSpecification();
        }
    }

    class NorthAmericaVechicleFactory implements VechicleFactory {

        @Override
        public Vechicle createCar() {
            return new Sedan();
        }

        @Override
        public Vechicle createBike() {
            return new FeltBike();
        }

        @Override
        public VechicleSpecification createSpecification() {
            return new NorthAmericaVechicleSpecification();
        }
    }

    interface VechicleSpecification {
        void dispay();
    }

    class EuropeVechicleSpecification implements VechicleSpecification {

        @Override
        public void dispay() {
            System.out.println("Europe Vechicle Specification: Fuel efficiency and emissions compliant with EU standards");
        }
    }

    class NorthAmericaVechicleSpecification implements VechicleSpecification {

        @Override
        public void dispay() {
            System.out.println("North America Vechicle Specification: Safety features compliant with local regulations");
        }
    }

    interface Vechicle {
        void assemble();
    }

    class HatchBack implements Vechicle {

        @Override
        public void assemble() {
            System.out.println("Assembling HatchBack");
        }
    }

    class Sedan implements Vechicle {

        @Override
        public void assemble() {
            System.out.println("Assembling Sedan");
        }
    }

    class FeltBike implements Vechicle {

        @Override
        public void assemble() {
            System.out.println("Assembling Felt Bike");
        }
    }

class BianchiBike implements Vechicle {

    @Override
    public void assemble() {
        System.out.println("Assembling Bianchi Bike");
    }
}
