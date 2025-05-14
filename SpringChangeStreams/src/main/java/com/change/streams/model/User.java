package com.change.streams.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document("user")
@Data
@Builder
public class User {
    
    @Id
    private String id;
    private String name;
    private Address address;
}
