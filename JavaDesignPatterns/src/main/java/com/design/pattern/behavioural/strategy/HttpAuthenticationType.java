package com.design.pattern.behavioural.strategy;

import java.util.Map;

public interface HttpAuthenticationType {
    void authenticate(Map<String, Object> credetials);

    AuthType getType();
}
