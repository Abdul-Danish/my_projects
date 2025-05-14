package com.change.streams.model;

import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;

@Data
@Builder
public class Entity {
    
    @Id
    @Generated
    private String id;
    private String name;
    private String alias;
    private String description;
    private String solutionId;
    private String alternateId;
    @Builder.Default
    private String version = "1.0";
    private boolean deleted;
//    private EntityStatus status;
    @DiffIgnore
    private String errorMessage;

    private String entityId;

    @DiffIgnore
    private List<String> versionHistory;

//    public void setStatus(EntityStatus status) {
//        if (EntityStatus.ARCHIVED.equals(status)) {
//            this.deleted = Boolean.TRUE;
//        }
//        this.status = status;
//    }

    public String getEntityType() {
        return "Entity";
    }

}
