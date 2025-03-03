package com.dsa.streams;

import lombok.Data;

@Data
public class Employee {

    private int id;
    private String name;
    private int age;
    private Gender gender;

    public Employee(int id, String name, int age, Gender gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}
