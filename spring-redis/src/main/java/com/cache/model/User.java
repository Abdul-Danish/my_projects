package com.cache.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("User-Redis")
public class User implements Serializable {
    
    private static final long serialVersionUID = -2924226644266137394L;

    @Id
    private String id;
    private String firstName;
    private String lastName;
    
}
