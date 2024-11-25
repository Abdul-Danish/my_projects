package com.cache.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = -8096991026165959844L;
    
    private String firstName;
    private String lastNmae;
    
}   
