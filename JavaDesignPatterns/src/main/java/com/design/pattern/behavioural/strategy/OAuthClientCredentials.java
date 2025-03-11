package com.design.pattern.behavioural.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuthClientCredentials implements HttpAuthenticationType {

    @Override
    public void authenticate(Map<String, Object> credetials) {
        log.info("Authenticating User {} using ClientCredentials", credetials.get("user"));
    }

    @Override
    public AuthType getType() {
        return AuthType.CLIENT_CREDENTIALS;
    }
}
