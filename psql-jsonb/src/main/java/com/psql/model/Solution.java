package com.psql.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name =  "solutions")
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Solution extends BaseEntity {

    @JdbcTypeCode(SqlTypes.JSON)
//    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = "jsonb")
    @Column(name = "template", columnDefinition = "jsonb")
    private SolutionTemplate template;

//    @Enumerated(EnumType.STRING)
    private SolutionType type = SolutionType.INTEGRATION;
    
    @JdbcTypeCode(SqlTypes.JSON)
//    @Type(type = "jsonb")
    @Column(name = "config", columnDefinition = "jsonb")
    private Map<String, Object> config;
    
    @JdbcTypeCode(SqlTypes.JSON)
//    @Type(type = "jsonb")
    @Column(name = "deployment", columnDefinition = "jsonb")
    private SolutionDeployment deployment;
    
    @JdbcTypeCode(SqlTypes.JSON)
//    @Type(type = "jsonb")
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;
    
    @JdbcTypeCode(SqlTypes.JSON)
//    @Type(type = "jsonb")
    @Column(name = "users", columnDefinition = "jsonb")
    private List<UserShare> users = new ArrayList<>();
    
    private Boolean enabled = false;
    private String blueprintId;
    private String exportId;
    
//    @Type(type = "jsonb")
//    private TemplateFile file;

    
}
