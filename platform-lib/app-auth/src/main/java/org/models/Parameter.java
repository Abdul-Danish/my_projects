package org.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter implements Serializable {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    private String name;

    private String type;
    private String value;

    private String scriptFormat;
    private String binding;
    private String version;
    private String resultVariable;

    private List<String> list;

    private Map<String, String> map;
}
