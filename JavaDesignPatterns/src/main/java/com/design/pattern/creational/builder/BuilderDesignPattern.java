package com.design.pattern.creational.builder;

import lombok.Data;

/*
 * Separate the construction of a complex object from its representation so that the same 
 * construction process can create different representations
 */
public class BuilderDesignPattern {

    public static void main(String args[]) {
        Employee employee = EmployeeBuilder.builder().setName("Demo User").setId(10).setRole("Developer").build();
        System.out.println(employee.toString());
    }
}

@Data
class Employee {
    private String name;
    private int id;
    private String role;
}

class EmployeeBuilder {
    private String name;
    private int id;
    private String role;

    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    public EmployeeBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EmployeeBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public EmployeeBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public Employee build() {
        Employee employee = new Employee();
        employee.setName(this.name);
        employee.setId(this.id);
        employee.setRole(this.role);
        return employee;
    }
}
