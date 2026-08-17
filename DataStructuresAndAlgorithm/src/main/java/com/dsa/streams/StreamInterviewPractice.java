package com.dsa.streams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

public class StreamInterviewPractice {

    public static void main(String[] args) {
        
        // Q. max number
        List<Integer> numbers = Arrays.asList(10, 25, 3, 47, 18, 52);
        Optional<Integer> max = numbers.stream().max(Comparator.comparingInt(value -> value));
        
        Optional<Integer> max2 = numbers.stream().max(Integer::compareTo);
        System.out.println("Max: " + max2);
        
        // Q. Find the second-highest number
        List<Integer> numbers2 = Arrays.asList(10, 25, 30, 25, 50, 40, 50);
        Optional<Integer> secondMax =  numbers2.stream().distinct().
            filter(num -> num != numbers2.stream().max(Integer::compareTo).get()).max(Integer::compareTo);
        
        Optional<Integer> secondMax2 = numbers2.stream().distinct().sorted(Comparator.reverseOrder()).skip(1).findFirst();
        System.out.println("second max: " + secondMax2);
        
        // Q. Find Duplicate numbers
        List<Integer> numbers3 = Arrays.asList(1, 2, 3, 2, 4, 5, 3, 6, 5);
        Map<Integer, Long> uniqueCount = numbers3.stream().collect(Collectors.groupingBy(num -> num, Collectors.counting()));
        uniqueCount.entrySet().stream().filter(entry -> entry.getValue() > 1).forEach(entry -> System.out.print(entry.getKey() + " "));
        System.out.println();
        
        // Q. Find the first non-repeated character
        String str = "swiss";
        // This one uses HashMap by default (not good if order is required)
        Map<Character, Long> nonRepeated = str.chars().mapToObj(ch -> (char) ch).collect(Collectors.groupingBy(ch -> ch, Collectors.counting()));
        Entry<Character, Long> res1 = nonRepeated.entrySet().stream().filter(entry -> entry.getValue() == 1l).findFirst().get();
        
        Map<Character, Long> nonRepeated2 = str.chars().mapToObj(ch -> (char) ch)
            .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        Entry<Character, Long> res2 = nonRepeated.entrySet().stream().filter(entry -> entry.getValue() == 1l).findFirst().get();
        System.out.println(res2.getKey());
        
        // Q. Group employee names by department
        List<Employee> employees = Arrays.asList(
            new Employee("Abdul", "IT", 90000),
            new Employee("John", "HR", 50000),
            new Employee("Mike", "IT", 70000),
            new Employee("Sara", "HR", 55000),
            new Employee("David", "Finance", 80000)
        );
        
        Map<String, List<String>> groupEmp = employees.stream()
            .collect(Collectors.groupingBy(emp -> emp.getDepartment(), Collectors.mapping(emp -> emp.getName(), Collectors.toList())));
        System.out.println("emp group: " + groupEmp);
        
        
        // Q. Find the highest-paid employee in each department
        Map<String, Optional<Employee>> deptWithMaxSalaryOpt = employees.stream()
            .collect(Collectors.groupingBy(emp -> emp.getDepartment(), Collectors.maxBy(Comparator.comparingInt(emp -> emp.getSalary()))));
        
        // without Optional
        Map<String, Employee> deptWithMaxSalary = employees.stream()
            .collect(Collectors.groupingBy(emp -> emp.getDepartment(), 
                Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingInt(emp -> emp.getSalary())), Optional::get)));
        System.out.println("deptWithMaxSalary: " + deptWithMaxSalary);
        
        // Q. Find the second-highest salary in each department        
        Map<String, Optional<Employee>> secondHighestSalaryPerDept = employees.stream().collect(Collectors.groupingBy(emp -> emp.getDepartment(), 
            Collectors.collectingAndThen(Collectors.toList(), list ->
                list.stream()
                .distinct().sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .skip(1).findFirst()
                )
            ));
        System.out.println("secondHighestSalaryPerDept: " + secondHighestSalaryPerDept);
        
        // Q. 
        
    }
    
    @Data
    @AllArgsConstructor
    static class Employee {
        String name;
        String department;
        int salary;
    }
    
}
