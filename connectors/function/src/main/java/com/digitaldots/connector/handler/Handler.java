package com.digitaldots.connector.handler;

import java.util.Map;

public interface Handler {

     public Map<String, Object> handle(Map<String, Map<String, Object>> request, Map<String, Map<String, String>> headers);
    
}
