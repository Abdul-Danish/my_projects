package com.design.pattern.behavioural.chainofresponsibility;

import lombok.Getter;
import lombok.Setter;

/*
 * This pattern is frequently used in the chain of multiple objects, where each object either handles the 
 * request or passes it on to the next object in the chain if it is unable to handle that request.
 */
public class ChainOfRespDesignPattern {

    public static void main(String[] args) {
        SupportHandler level1Support = new Level1SupportHandler();
        SupportHandler level2Support = new Level2SupportHandler();
        SupportHandler level3Support = new Level3SupportHandler();

        level1Support.handleNext(level2Support);
        level2Support.handleNext(level3Support);

        Request request1 = new Request(Priority.BASIC);
        Request request2 = new Request(Priority.INTERMEDIATE);
        Request request3 = new Request(Priority.CRITICAL);

        level1Support.handle(request1);
        System.out.println();
        level1Support.handle(request2);
        System.out.println();
        level1Support.handle(request3);
    }
}

enum Priority {
    BASIC, INTERMEDIATE, CRITICAL
}

@Getter
@Setter
class Request {
    private Priority priority;

    public Request(Priority priority) {
        this.priority = priority;
    }
}

interface SupportHandler {
    void handle(Request request);

    void handleNext(SupportHandler handler);
}

class Level1SupportHandler implements SupportHandler {
    private SupportHandler handler;

    @Override
    public void handle(Request request) {
        if (request.getPriority().equals(Priority.BASIC)) {
            System.out.println("Request Handled by level 1 Support");
        } else {
            System.out.println("Forwarding to Level-2");
            handler.handle(request);
        }
    }

    @Override
    public void handleNext(SupportHandler handler) {
        this.handler = handler;
    }
}

class Level2SupportHandler implements SupportHandler {
    private SupportHandler handler;

    @Override
    public void handle(Request request) {
        if (request.getPriority().equals(Priority.INTERMEDIATE)) {
            System.out.println("Request Handled by level 2 Support");
        } else {
            System.out.println("Forwarding to Level-3");
            handler.handle(request);
        }
    }

    @Override
    public void handleNext(SupportHandler handler) {
        this.handler = handler;
    }
}

class Level3SupportHandler implements SupportHandler {
    @Override
    public void handle(Request request) {
        if (request.getPriority().equals(Priority.CRITICAL)) {
            System.out.println("Request Handled by level 3 Support");
        } else {
            System.out.println("Cannot Handle Current Request");
        }
    }

    @Override
    public void handleNext(SupportHandler handler) {
    }
}
