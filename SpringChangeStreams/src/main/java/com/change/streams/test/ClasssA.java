package com.change.streams.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasssA {

    @Autowired
    private Test test;
    
    public String display() {
        return test.test();
    }

}
