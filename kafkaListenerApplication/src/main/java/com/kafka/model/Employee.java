package com.kafka.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String id;
    private String empName;
}
