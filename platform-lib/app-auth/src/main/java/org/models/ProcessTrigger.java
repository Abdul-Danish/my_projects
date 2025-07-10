package org.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ProcessTrigger implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7857529127164550536L;

    private String id;
    private String type;
    private String tenantId;
    private String solutionId;
    private String processId;
    private String activityId;
    private String name;
    private String value;
    private String connectorId;
    private String connectorVersion;
    private Map<String, Object> properties;
    private Map<String, List<Parameter>> io;
}
