package com.dsa.practice;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class Test {

    @Autowired
    private static classA a;

    public static void main(String[] args) throws CloneNotSupportedException, IOException {
        
        // Static polymorphism (method overloading) / Dynamic polymorphism (method overriding)
        classA ab = new classB();
        classA a = ab;
        classB b = (classB) ab;   // cannot implicitly cast because 'ab' is reference of classA (need explicit cast)
        b.cb();
        
        // immutable fields
        ImmutableReminder ir = new ImmutableReminder(new Date());
        System.out.println("ir: " + ir.getRemindingDate());
        
        // cloning
        NestedObj nestedObj = new NestedObj("NO1");
        ObjA objA1 = new ObjA("OB1", nestedObj);

        ObjA objA2 = (ObjA) objA1.clone();
        objA2.setO1("OB2");
        objA2.getNestedObj().setN1("NO2");
        System.out.println("A1: " + objA1);
        System.out.println("A2: " + objA2);

        // Time conversion
        System.out.println("Date: " + LocalDate.now().toString());
        String time = "07:24";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.US);
        LocalTime localTime = LocalTime.parse(time, formatter);

//        ZoneId zone = ZoneId.of("America/Chicago");
        ZoneOffset offset = ZoneOffset.ofHours(0);
        ZonedDateTime tz = ZonedDateTime.of(LocalDate.now(), localTime, offset);
        System.out.println("tz:" + tz.toString());
        System.out.println("tz:" + tz.toInstant().toString());

        // UriComponentsBuilder
        Map<String, String> params = new HashMap<>();
        params.put("location", "en-us");
        URI uri = UriComponentsBuilder.fromUriString("http://dataservice.accuweather.com/forecasts/v1/daily/1day/{location}")
            .buildAndExpand(params).toUri();
        System.out.println("URI: {}" + uri);

        // ConcurrentHashMap
        ConcurrentHashMap<String, Object> cm = new ConcurrentHashMap<>();
        // 'k' is the key with empty object as value (can be anything)
        // ConcurrentHashMap because multiple threads cannot call 'computeIfAbsent' for race condition.
        cm.computeIfAbsent("danish", k -> {
            System.out.println("key: " + k);
            return new Object();
        });
        System.out.println("Get: " + cm.get("danish"));

        //
        String str = "tst";
        Supplier<Integer> len = str::length;
        System.out.println(len.get());
        //

        // Func Interface
        Predicate<String> predicate = new Predicate<String>() {
            @Override
            public boolean test(String t) {
                return t.equals("id1");
            }
        };

        ArrayList<String> ar = new ArrayList<>();
        ar.ensureCapacity(0);
        
        List<String> list = new ArrayList<>();
        list.add("id1");
        List<String> collect = list.stream().filter(predicate).collect(Collectors.toList());
        for (String string : collect) {
            System.out.println(string);
        }

        // problem
        // String binary = "110001011";
        String binary = "11111000000";

        boolean isBinary = split(binary, 0, true);
        System.out.println("is Binary: " + isBinary);        
    }

    private static boolean split(String str, int len, boolean isValid) {

        int i_count = 0;
        int o_count = 0;
        while (len < str.length() && isValid) {
            for (int j = 0; j <= len; j++) {
                if ('1' == str.charAt(j)) {
                    i_count++;
                } else {
                    o_count++;
                }
            }

//            System.out.println(isValid);
            if (i_count < o_count) {
                return false;
            }
            len++;
            isValid = split(str, len, isValid);
        }
        return isValid;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ObjA implements Cloneable {
        private String o1;
        private NestedObj nestedObj;

        @Override
        public Object clone() throws CloneNotSupportedException {
            // shallow copy
            // return super.clone();
            
            // Deep copy
            ObjA a = new ObjA();
            a.setO1(this.o1);
            a.setNestedObj(new NestedObj(this.nestedObj.getN1()));
            return a;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class NestedObj {
        private String n1;
    }
    
    interface dummyInterface {
        void test();
    }

    @Component
    static class classA implements dummyInterface {
        public void test() {
            System.out.println("Inside Class A");
        }
        
        public void ca() {
            System.out.println("class A");
        }
    }

    @Component
    static class classB extends classA implements dummyInterface {
        public void test() {
            System.out.println("Inside Class B");
        }
        
        public void cb() {
            System.out.println("class B");
        }
    }
    
    static class ImmutableReminder {
        private final Date remindingDate;
        
        public ImmutableReminder(Date remindingDate) {
            System.out.println("rm: " + remindingDate.getTime() + " ctm: " + System.currentTimeMillis());
            if (remindingDate.getTime() < System.currentTimeMillis()) {
                throw new IllegalArgumentException("cannot set reminder for past time: " + remindingDate);
            }
            this.remindingDate = new Date(remindingDate.getTime());
        }
        
        public Date getRemindingDate() {
            return (Date) this.remindingDate.clone();
        }   
    }
    
}
