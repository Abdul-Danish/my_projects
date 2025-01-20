package com.digitaldots.connector.core;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class GenericResponseImpl implements GenericResponse {

    private JSONObject payload;
    
    public GenericResponseImpl(JSONObject payload) {
        this.payload = payload;
    }
    
    @Override
    public JSONObject getResponse() {
        return payload;
    }

}
