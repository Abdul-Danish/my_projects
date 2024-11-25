package com.psql.dto;

import java.util.List;
import java.util.Map;

import com.psql.model.EntityStatus;
import com.psql.model.SolutionDeployment;
import com.psql.model.SolutionTemplate;
import com.psql.model.SolutionType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolutionDto {

//    private String id;
    private String alias;
    private Map<String, Object> config;

    private boolean deleted;
    private SolutionDeployment deployment;
//    @NotNull(message = "Description is required")
    private String description;
    private String errorMessage;
//    @NotNull(message = "Solution Name is required")
    private String name;
    private EntityStatus status;
    private List<String> tags;
    private SolutionTemplate template;
    private SolutionType type;
    private String version = "1.0";
    
}
