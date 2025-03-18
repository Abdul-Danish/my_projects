package com.design.pattern.structural.facade;

/*
 * Facade defines a high-level interface that makes the subsystem easier to use.
 * 
 * The facade exposes existing functionality and the mediator adds to the existing functionality.
 * 
 * (facade does the internal calls to simplify complexcity whereas for mediator the objects responds 
 *  back to the mediator for additional and so on)
 * 
 * Similar To: Mediator Design Pattern 
 */
public class FacadeDesignPattern {

    public static void main(String[] args) {
        Order_Facade order_Facade = new Order_Facade();
        order_Facade.orderFood("Biryani");
    }
}

class Order_Facade {
    private Waiter_subsystem_1 waiter_subsystem_1;
    private kitchen_subsystem_2 kitchen_subsystem_2;
    private Cleaner_subsystem_3 cleaner_subsystem_3;

    public void orderFood(String order) {
        waiter_subsystem_1 = new Waiter_subsystem_1(order);
        kitchen_subsystem_2 = new kitchen_subsystem_2(order);
        cleaner_subsystem_3 = new Cleaner_subsystem_3();

        waiter_subsystem_1.writeOrder();
        waiter_subsystem_1.sendToKitchen();
        kitchen_subsystem_2.prepareOrder();
        kitchen_subsystem_2.callWaiter();
        waiter_subsystem_1.serveOrder();
        cleaner_subsystem_3.cleanDishes();
    }
}

class Waiter_subsystem_1 {
    private String order;

    public Waiter_subsystem_1(String order) {
        this.order = order;
    }

    public void writeOrder() {
        System.out.println("Waiter writes client's order");
    }

    public void sendToKitchen() {
        System.out.println("Send '" + order + "' order to kitchen");
    }

    public void serveOrder() {
        System.out.println("Serve order to client");
    }
}

class kitchen_subsystem_2 {
    private String order;

    public kitchen_subsystem_2(String order) {
        this.order = order;
    }

    public void prepareOrder() {
        System.out.println("Preparing order: " + order);
    }

    public void callWaiter() {
        System.out.println("Call Waiter");
    }
}

class Cleaner_subsystem_3 {

    public void cleanDishes() {
        System.out.println("Clean the dishes");
    }
}
