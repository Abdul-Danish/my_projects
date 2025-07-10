package org.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
//implements Serializable {

//    private static final long serialVersionUID = 1L;
    private String id;
    private String empName;
    
}
