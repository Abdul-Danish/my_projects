package com.design.pattern.creational.factory;

/*
 * This pattern is particularly useful when the exact types of objects to be created may vary or need to be determined at runtime,
 * enabling flexibility and extensibility in object creation.
 * 
 * Creates objects directly from the factory
 * 
 * Objects are created dynamically (Note: Not pre-created like Strategy Patten)
 * 
 * Similar To: Strategy Pattern, Abstract Design Pattern
 */
public class FactoryDesignPattern {

    public static void main(String[] args) {
        // Creating Object directly from Factory
        PizzaFactory pizzaFactory = new MagharitaPizzaFactory();
        Pizza pizza = pizzaFactory.createPizza();
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
    }
}

interface Pizza {
    public static final String PEPPERON = "Pepperon";
    public static final String MARGHERITA = "Margherita";

    void prepare();

    void bake();

    void cut();

    void box();
}

class PepperonPizza implements Pizza {

    @Override
    public void prepare() {
        System.out.println("Preparing " + PEPPERON + " Pizza");
    }

    @Override
    public void bake() {
        System.out.println("Baking " + PEPPERON + " Pizza");
    }

    @Override
    public void cut() {
        System.out.println("Cutting...");
    }

    @Override
    public void box() {
        System.out.println("Boxing");
    }

}

class MargharitaPizza implements Pizza {

    @Override
    public void prepare() {
        System.out.println("Preparing " + MARGHERITA + " Pizza");
    }

    @Override
    public void bake() {
        System.out.println("Baking " + MARGHERITA + " Pizza");
    }

    @Override
    public void cut() {
        System.out.println("Cutting...");
    }

    @Override
    public void box() {
        System.out.println("Boxing");
    }

}

interface PizzaFactory {
    Pizza createPizza();
}

class PepperonPizzaFactory implements PizzaFactory {

    @Override
    public Pizza createPizza() {
        return new PepperonPizza();
    }
}

class MagharitaPizzaFactory implements PizzaFactory {

    @Override
    public Pizza createPizza() {
        return new MargharitaPizza();
    }
}