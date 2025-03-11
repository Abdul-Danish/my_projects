package com.design.pattern.structural.decorator;

/*
 * 
 */
public class DecoratorDesignPattern {

    public static void main(String args[]) {
        Cofee plainCofee = new PlainCofee("plain cofee with decorated whip cream", 20);
        MilkDecorator milkDecorator = new MilkDecorator(plainCofee, 5);
        SugarDecorator sugarDecorator = new SugarDecorator(milkDecorator, 5);
        System.out.println("Description: " + sugarDecorator.getDescription());
        System.out.println("Cost: " + sugarDecorator.getCost());
    }
}

interface Cofee {
    String getDescription();

    double getCost();
}

class PlainCofee implements Cofee {
    private String description;
    private double cost;

    public PlainCofee(String description, int cost) {
        this.description = description;
        this.cost = cost;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public double getCost() {
        return this.cost;
    }
}

abstract class CofeeDecorator implements Cofee {
    protected Cofee cofee;

    public CofeeDecorator(Cofee cofee) {
        this.cofee = cofee;
    }

    @Override
    public String getDescription() {
        return cofee.getDescription();
    }

    @Override
    public double getCost() {
        return cofee.getCost();
    }
}

class MilkDecorator extends CofeeDecorator {
    private double additionalCost;

    public MilkDecorator(Cofee cofee, double additionalCost) {
        super(cofee);
        this.additionalCost = additionalCost;
    }

    @Override
    public String getDescription() {
        return cofee.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return cofee.getCost() + additionalCost;
    }
}

class SugarDecorator extends CofeeDecorator {
    private double additionalCost;

    public SugarDecorator(Cofee cofee, double additionalCost) {
        super(cofee);
        this.additionalCost = additionalCost;
    }

    @Override
    public String getDescription() {
        return cofee.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return cofee.getCost() + additionalCost;
    }
}