package com.secret.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
//@Secret(backend = "keys", value = "sample")
@ConfigurationProperties("danish")
public class Credentials {

//    @Id
    private String username;
    private String password;
    
}

