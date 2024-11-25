package com.web.sso.model;

import java.util.ArrayList;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResourceServer {

    private String name;
    private String identifier;
    private ArrayList<Map<String, String>> scopes;
}
