package com.design.pattern.creational.factory;

/*
 * This pattern is particularly useful when the exact types of objects to be created may vary or need to be determined at runtime,
 * enabling flexibility and extensibility in object creation.
 */
public class FactoryDesignPattern {

    public static void main(String[] args) {
        AnimalFactory catFactory = new CatFactory();
        Client client = new Client(catFactory);
        Animal animal = client.getAnimal();
        animal.printType();
    }
}

abstract class Animal {
    public abstract void printType();
}

class Cat extends Animal {
    @Override
    public void printType() {
        System.out.println("Cat");
    }
}

class Dog extends Animal {
    @Override
    public void printType() {
        System.out.println("Dog");
    }
}

interface AnimalFactory {
    Animal createAnimal();
}

class CatFactory implements AnimalFactory {
    @Override
    public Animal createAnimal() {
        return new Cat();
    }
}

class DogFactory implements AnimalFactory {
    @Override
    public Animal createAnimal() {
        return new Dog();
    }
}

class Client {
    private Animal animal;

    public Client(AnimalFactory animalFactory) {
        animal = animalFactory.createAnimal();
    }

    public Animal getAnimal() {
        return animal;
    }
}