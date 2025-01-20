package com.digitaldots.connector.core;

import java.util.Map;

public interface ConnectorRequest<R extends ConnectorResponse> {

    R execute();
    
    void setRequestParameters(Map<String, Object> requestParams);
    
    Map<String, Object> getRequestParameters();
    
}
