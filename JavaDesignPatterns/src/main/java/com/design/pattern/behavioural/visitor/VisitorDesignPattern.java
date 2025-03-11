package com.design.pattern.behavioural.visitor;

import java.util.ArrayList;
import java.util.List;

/*
 * The Visitor design pattern is a behavioral pattern that allows you to add new operations to a 
 * group of related classes without modifying their structures.
 * 
 * Similar To: -
 */
public class VisitorDesignPattern {

    public static void main(String[] args) {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle(5));
        shapes.add(new Square(10));

        AreaCalculator areaCalculator = new AreaCalculator();
        shapes.forEach(shape -> {
            System.out.println(shape.accept(areaCalculator));
        });
    }
}

interface ShapesVisitor {
    double visit(Circle circle);

    double visit(Square square);
}

interface Shape {
    double accept(ShapesVisitor shapesVisitor);
}

class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double accept(ShapesVisitor shapesVisitor) {
        return shapesVisitor.visit(this);
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return this.radius;
    }
}

class Square implements Shape {
    private double sideOfSquare;

    public Square(double sideOfSquare) {
        this.sideOfSquare = sideOfSquare;
    }

    @Override
    public double accept(ShapesVisitor shapesVisitor) {
        return shapesVisitor.visit(this);
    }

    public void setSideOfSquare(double sideOfSquare) {
        this.sideOfSquare = sideOfSquare;
    }

    public double getSideOfSquare() {
        return this.sideOfSquare;
    }
}

class AreaCalculator implements ShapesVisitor {

    @Override
    public double visit(Circle circle) {
        System.out.println("Calculating Circle Area");
        return Math.PI * Math.pow(circle.getRadius(), 2);
    }

    @Override
    public double visit(Square square) {
        System.out.println("Calculating Square Area");
        return Math.pow(square.getSideOfSquare(), 2);
    }

}