//package com.batch.process.test;
//
//import org.hibernate.internal.build.AllowSysOut;
//
//public class Main {
//
//    public static void main(String[] args) {
//
//        SampleRecord obj = new SampleRecord("val1", "val2");
//        SampleRecord obj2 = new SampleRecord("val1", "val2");
//        SampleRecord obj3 = new SampleRecord("val3", "val4");
//
////        System.out.println(obj.col1());
////        System.out.println(obj.col2());
////        System.out.println(obj.hashCode());
////        System.out.println(obj.toString());
////        System.out.println(obj.equals(obj2));
////        System.out.println(obj.equals(obj3));
//
//        LombokClass class1 = new LombokClass("v1", "v2");
//        System.out.println(class1.toString());
//        System.out.println(class1);
//
//        howMany(1);
//        
//        String result = switch (1) {
//        case 1 -> { 
//            String str = "one";
//            yield str;
//        }
//        case 2 -> "two";
//        default -> "many";
//        };
//        System.out.println(result);
//    }
//
//    static void howMany(int k) {
//        switch (k) {
//        case 1 -> System.out.println("one");
//        case 2 -> System.out.println("two");
//        default -> System.out.println("many");
//        }
//    }
//
//}
