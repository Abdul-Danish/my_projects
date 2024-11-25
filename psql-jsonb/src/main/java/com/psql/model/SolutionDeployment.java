package com.psql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolutionDeployment {
    private static final long serialVersionUID = 8585599230991867847L;
    private String environment;
    private String alias;
    @Builder.Default
    private Boolean isCloud = Boolean.FALSE;
    private String mode;
    private String storage;
    private String targetCloud;
    
    private NestedField nestedField;
}


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class NestedField {
    private String tempField;
}
