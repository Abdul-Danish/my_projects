package com.psql.model;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
//@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(value = Include.NON_NULL)
//@Entity
@MappedSuperclass
public class BaseEntity extends SampleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String alias;
    private String description;
    private String solutionId;
    private String alternateId;
    private String version = "1.0";
    private boolean deleted;
    @Enumerated(EnumType.STRING)
    private EntityStatus status;
//    @DiffIgnore
//    private String errorMessage;

    private String entityId;

//    @DiffIgnore
//    private List<String> versionHistory;

    public void setStatus(EntityStatus status) {
        if (EntityStatus.ARCHIVED.equals(status)) {
            this.deleted = Boolean.TRUE;
        }
        this.status = status;
    }

    public String getEntityType() {
        return "Entity";
    }
    
}
