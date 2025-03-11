package com.design.pattern.behavioural.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuthCodeGrant implements HttpAuthenticationType {

    @Override
    public void authenticate(Map<String, Object> credentials) {
        log.info("authenticating user {} using OAuthCodeGrant", credentials.get("user"));
    }

    @Override
    public AuthType getType() {
        return AuthType.OAUTH_CODE_GRANT;
    }
}
