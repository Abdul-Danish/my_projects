package com.design.pattern.structural.composite;

import java.util.ArrayList;
import java.util.List;

/*
 * The Composite Design Pattern was created to address specific challenges related to the representation and manipulation of hierarchical structures in a uniform way
 */
public class CompositeDesignPattern {

    public static void main(String args[]) {
        SimpleTask simpleTask1 = new SimpleTask("complete coding");
        SimpleTask simpleTask2 = new SimpleTask("write implementation");

        TaskList projectLists = new TaskList("Project Lists");
        projectLists.addTask(simpleTask1);
        projectLists.addTask(simpleTask2);

        TaskList phase1Tasks = new TaskList("Phase 1 Tasks");
        phase1Tasks.addTask(new SimpleTask("Design"));
        phase1Tasks.addTask(new SimpleTask("Implementations"));

        projectLists.addTask(phase1Tasks);

        phase1Tasks.display();
        System.out.println();
        projectLists.display();
    }
}

// Component
interface Task {
    String getTitle();

    void setTitle(String title);

    void display();
}

// Leaf
class SimpleTask implements Task {
    private String title;

    public SimpleTask(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void display() {
        System.out.println("Simple Task: " + title);
    }
}

// Composite
class TaskList implements Task {
    private String title;
    private List<Task> tasklist;

    public TaskList(String title) {
        this.title = title;
        tasklist = new ArrayList<>();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    public void addTask(Task task) {
        tasklist.add(task);
    }

    public void removetask(Task task) {
        tasklist.remove(task);
    }

    @Override
    public void display() {
        System.out.println("Task List: " + title);
        tasklist.stream().forEach(task -> task.display());
    }
}
