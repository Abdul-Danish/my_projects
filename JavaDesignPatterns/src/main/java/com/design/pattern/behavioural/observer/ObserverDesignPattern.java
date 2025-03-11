package com.design.pattern.behavioural.observer;

import java.util.ArrayList;
import java.util.List;

/*
 * It defines a one-to-many dependency between objects. When one object (the subject) changes 
 * state, all its dependents (observers) are notified and updated automatically. It primarily deals 
 * with the interaction and communication between objects, specifically focusing on how objects 
 * behave in response to changes in the state of other objects
 * 
 * Similar To: -
 */
public class ObserverDesignPattern {

    public static void main(String[] args) {
        WeatherStation weatherStation = new WeatherStation();
        Subscriber1 subscriber1 = new Subscriber1();
        Subscriber2 subscriber2 = new Subscriber2();
        Subscriber3 subscriber3 = new Subscriber3();
        weatherStation.registerObserver(subscriber1);
        weatherStation.registerObserver(subscriber2);
        weatherStation.registerObserver(subscriber3);

        weatherStation.setWeather("cold");
        System.out.println();
        weatherStation.setWeather("warm");
    }
}

interface Subject {
    void registerObserver(Observer observer);

    void removeObserver(Observer observer);

    void setWeather(String weather);
}

class WeatherStation implements Subject {
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void setWeather(String weather) {
        updateObserver(weather);
    }

    private void updateObserver(String weather) {
        for (Observer observer : observers) {
            observer.update(weather);
        }
    }
}

interface Observer {
    void update(String weather);
}

class Subscriber1 implements Observer {
    private String weather;

    @Override
    public void update(String weather) {
        this.weather = weather;
        display();
    }

    public void display() {
        System.out.println("Weather updated for sub1: " + this.weather);
    }
}

class Subscriber2 implements Observer {
    private String weather;

    @Override
    public void update(String weather) {
        this.weather = weather;
        display();
    }

    public void display() {
        System.out.println("Weather updated for sub2: " + this.weather);
    }
}

class Subscriber3 implements Observer {
    private String weather;

    @Override
    public void update(String weather) {
        this.weather = weather;
        display();
    }

    public void display() {
        System.out.println("Weather updated for sub3: " + this.weather);
    }
}
