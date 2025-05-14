package com.change.streams.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassB {
    
    @Autowired
    private ClasssA classA;
    
    public void temp() {
        System.out.println("Obj: " + classA);
//        classA.display();
    }
    
    public static void main(String args[]) {
//        ClasssA classA = new ClasssA();
//        classA.display();
        
//        temp();
    }

}
