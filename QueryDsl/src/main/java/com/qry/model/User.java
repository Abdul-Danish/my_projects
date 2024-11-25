package com.qry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.querydsl.core.annotations.QueryEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@QueryEntity
@Data
@Document
//@Entity
//@Schema(implementation = Object.class)
public class User {

    @Id
//    @javax.persistence.Id
    private String id;
    private String name;
    private String bid;
    
}
