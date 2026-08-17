package com.dsa.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        List<Employee> sortByAgeAndName = employees.stream()
            .sorted(Comparator.comparing((Employee e) -> e.getAge()).thenComparing((Employee e) -> e.getName()))
            .collect(Collectors.toList());
//        sortByAgeAndName.forEach(System.out::println);

        // (or)

        List<Employee> reverseSortByAgeAndName = employees.stream()
            .sorted(Comparator.comparing(Employee::getAge).thenComparing(Employee::getName).reversed()).collect(Collectors.toList());
//        reverseSortByAgeAndName.forEach(System.out::println);

        // AllMatch
        boolean allMatch1 = employees.stream().allMatch(emp -> emp.getName().equals("emp3"));

        // (or)

        Predicate<Employee> isEmp = emp -> emp.getName().equals("emp3");
        boolean allMatch2 = employees.stream().allMatch(isEmp);
//        System.out.println(allMatch);

        // AnyMatch
        boolean anyMatch1 = employees.stream().anyMatch(emp -> emp.getName().equals("emp3"));

        // (or)

        Predicate<Employee> isEmp3 = emp -> emp.getName().equals("emp3");
        Predicate<Employee> isEmp10 = emp -> emp.getName().equals("emp10");
        Predicate<Employee> isEmp3OrEmp10 = isEmp3.or(isEmp10);
        boolean anyMatch2 = employees.stream().anyMatch(isEmp3OrEmp10);
//        System.out.println(anyMatch2);

        // Max
        Optional<Employee> maxAge = employees.stream().max(Comparator.comparing(Employee::getAge));
        maxAge.ifPresent(emp -> System.out.println(emp));

        // Min
        Optional<Employee> minAge = employees.stream().min(Comparator.comparing(Employee::getAge));
//        minAge.ifPresent(emp -> System.out.println(emp));

        // Limit
        List<Employee> limitedEmployees = employees.stream().limit(3).collect(Collectors.toList());
//        System.out.println("Limited Emp: " + limitedEmployees);

        // Skip
        List<Employee> skipedEmployees = employees.stream().skip(4).collect(Collectors.toList());
//        System.out.println("Skipped Emp: " + skipedEmployees);

        // Group
        Map<Gender, Long> genderCount = employees.stream().collect(Collectors.groupingBy(Employee::getGender, Collectors.counting()));
        
        Map<Gender, List<Employee>> groupByGender = employees.stream().collect(Collectors.groupingBy(Employee::getGender));
//        groupByGender.entrySet().stream().forEach(genderGroup -> {
//            System.out.println(genderGroup.getKey());
//            genderGroup.getValue().forEach(System.out::println);
//            System.out.println();
//        });

        // Joining
        String collectNames = employees.stream().map(emp -> emp.getName()).collect(Collectors.joining(", "));
//        System.out.println("Emp Names: " + collectNames);

        // FlatMap
        List<List<Employee>> nestedEmpList = Arrays.asList(Arrays.asList(employee1, employee2), Arrays.asList(employee3, employee4));
        List<Employee> flatEmpList = nestedEmpList.stream().flatMap(List::stream).collect(Collectors.toList());
//        System.out.println(flatEmpList);

        // Partitioning
        Map<Boolean, List<Employee>> partitionedEmployees = employees.stream().collect(Collectors.partitioningBy(emp -> emp.getAge() > 30));
//         for (Entry<Boolean, List<Employee>> entry : partitionedEmployees.entrySet()) {
//             System.out.println("partitioned Emp: " + entry);
//         }

        // Reduce
        Integer reducedAge = employees.stream().map(emp -> emp.getAge()).reduce(0, (fst, snd) -> fst + snd);
        // (or)
//        Integer reducedAge = employees.stream().map(emp -> emp.getAge()).reduce(0, Integer::sum);
        // (or)
//         Integer reduceAge = employees.stream().map(emp -> emp.getAge()).reduce(0, (fst, snd) -> Integer.sum(fst, snd));
//        System.out.println("reduced Emp Age: " + reducedAge);

        // Count
        long count = employees.stream().filter(emp -> emp.getAge() > 30).distinct().count();
//        System.out.println("count: " + count);

//       Practice:
        List<Integer> intList = Arrays.asList(1, 2, 3, 5, 4, 5, 3, 7, 0);
        List<Integer> dubList = new ArrayList<>();

        List<Integer> collect = intList.stream()
            // (DSC)
            // .sorted(Comparator.reverseOrder())
            // (ASC)
            // .sorted(Integer::compareTo)
            // (or)
            // .sorted((i1, i2) -> i1.compareTo(i2))
            .collect(Collectors.toList());

        // (or by using collections)
        // Collections.sort(collect, Collections.reverseOrder());
        // System.out.println(collect);

        SortedSet<Integer> sortedSet = new TreeSet<>();
        sortedSet.addAll(intList);
        // System.out.println(sortedSet);

//        printArray(intList);
        
        
        String str = "abcabdef";
        
        Map<Character, Long> collect2 = str.chars().mapToObj(ch -> (char) ch).collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        System.out.println("tp: " + collect2);
        
        
        // sort integers
        List<Integer> test = Arrays.asList(2, 8, 4, 6, 1, 8, 5);
        List<Integer> srtlist = test.stream().sorted(Comparator.comparingInt(num -> num)).collect(Collectors.toList());
//        System.out.println("srt: " + srtlist);
        
        int[] arr = new int[]{1, 2, 3};
        Arrays.stream(arr).map(i -> i).forEach(i -> System.out.print(i + " "));
        Stream<int[]> of = Stream.of(arr);
//        of.map(i -> i).forEach(i -> System.out.print(i + " "));
        
    }

    public static <E, L extends List<E>> void printArray(L inputArray) {
        // Display array elements
        for (E element : inputArray) {
            System.out.printf("%s ", element);
        }
        System.out.println();
    }

}
