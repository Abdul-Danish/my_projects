package com.change.streams.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document("event")
@Data
@Builder
public class Event {

    @Id
    private String id;
    private Object payload;
    private int commitId;
    
}
