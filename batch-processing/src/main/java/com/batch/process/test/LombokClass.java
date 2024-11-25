package com.batch.process.test;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

@Value
@Getter(AccessLevel.NONE)
public class LombokClass {

    private String col1;
    private String col2;
    
}
