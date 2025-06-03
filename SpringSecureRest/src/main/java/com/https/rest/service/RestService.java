package com.https.rest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.springframework.stereotype.Service;

@Service
public class RestService {
    
    public static void main(String args[]) {
        List<int[]> list = new ArrayList<>();
        list.add(new int[] {1});
        list.add(new int[] {2});
        list.stream().forEach(item -> System.out.println("item: " + item[0]));
        
        List<String> vector = new Vector<>();
        vector.add("demo1");
        vector.add("demo2");
        System.out.println("vector data: "  + vector);
        
        Set<String> set = new LinkedHashSet<>();
        set.add("itm1");
        set.add("itm2");
        System.out.println("set: " + set);
    }

    public Map<String, String> getSecureData() {
        Map<String, String> response = new HashMap<>();
        response.put("result", "Success");
        return response;
    }

}
