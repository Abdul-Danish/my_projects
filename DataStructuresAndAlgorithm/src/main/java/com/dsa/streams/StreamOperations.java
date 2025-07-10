package com.dsa.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamOperations {

    public static void main(String args[]) {

        Employee employee1 = new Employee(1, "emp2", 20, Gender.MALE);
        Employee employee2 = new Employee(2, "emp1", 20, Gender.FEMALE);
        Employee employee3 = new Employee(3, "emp3", 32, Gender.FEMALE);
        Employee employee4 = new Employee(4, "emp4", 46, Gender.MALE);
        Employee employee5 = new Employee(5, "emp5", 34, Gender.FEMALE);
        Employee employee6 = new Employee(6, "emp6", 27, Gender.MALE);

        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);
        employees.add(employee3);
        employees.add(employee4);
        employees.add(employee5);
        employees.add(employee6);

        // Filter
        List<Employee> filterByGender = employees.stream().filter(emp -> emp.getGender().equals(Gender.FEMALE))
            .collect(Collectors.toList());
//        filterByGender.forEach(System.out::println);

        // Sort
        List<Employee> sortByAge1 = employees.stream()
            .sorted(Comparator.comparing((Employee e) -> e.getAge()).thenComparing((Employee e) -> e.getName()))
            .collect(Collectors.toList());

        // (or)

        List<Employee> sortByAge2 = employees.stream().sorted(Comparator.comparing(Employee::getAge).thenComparing(Employee::getName))
            .collect(Collectors.toList());
//        sortByAge1.forEach(System.out::println);

        // AllMatch
        boolean allMatch = employees.stream().allMatch(emp -> emp.getName().equals("emp3"));
//        System.out.println(allMatch);

        // AnyMatch
        boolean anyMatch = employees.stream().anyMatch(emp -> emp.getName().equals("emp3"));
//        System.out.println(anyMatch);

        // Max
        Optional<Employee> maxAge = employees.stream().max(Comparator.comparing(Employee::getAge));
//        maxAge.ifPresent(emp -> System.out.println(emp));

        // Min
        Optional<Employee> minAge = employees.stream().min(Comparator.comparing(Employee::getAge));
//        minAge.ifPresent(emp -> System.out.println(emp));

        // Group
        Map<Gender, List<Employee>> groupByGender = employees.stream().collect(Collectors.groupingBy(Employee::getGender));
//        groupByGender.entrySet().stream().forEach(genderGroup -> {
//            System.out.println(genderGroup.getKey());
//            genderGroup.getValue().forEach(System.out::println);
//            System.out.println();
//        });

        // FlatMap
        List<List<Employee>> nestedEmpList = Arrays.asList(Arrays.asList(employee1, employee2), Arrays.asList(employee3, employee4));
        List<Employee> flatEmpList = nestedEmpList.stream().flatMap(List::stream).collect(Collectors.toList());
//        System.out.println(flatEmpList);

//       Practice:
        
        List<Integer> intList = Arrays.asList(1, 2, 3, 5, 4, 5, 3, 7);
        List<Integer> dubList = new ArrayList<>();

        List<Integer> collect = intList.stream().sorted(Integer::compareTo)
//        .filter(num -> {
//            
//        })
        .collect(Collectors.toList());
        
        System.out.println(collect);
        
    }

}
