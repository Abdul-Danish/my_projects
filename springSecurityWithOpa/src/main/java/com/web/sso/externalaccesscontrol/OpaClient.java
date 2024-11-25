package com.web.sso.externalaccesscontrol;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpaClient {

    @Autowired
    private RequestTracker requestTracker;

    private String opaAuthUrl = "http://localhost:9000/v1/data/auth/user";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public OpaClient() {
        objectMapper = new ObjectMapper();
        restTemplate = new RestTemplate();
    }

    public boolean allow(String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> input = new HashMap<>();
        input.put("uri", requestTracker.getUri());
        input.put("method", requestTracker.getMethod());
        System.out.println("PRINCIPLE: " + authentication.getPrincipal());
        input.put("roles", ((DefaultOidcUser) authentication.getPrincipal()).getAttribute("User-Roles"));
        input.put("operation", operation);

        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.set("input", objectMapper.valueToTree(input));
        log.info("Input Info {}", requestNode.toPrettyString());

        JsonNode responseNode = restTemplate.postForObject(opaAuthUrl, requestNode, JsonNode.class);
        log.info("response Info {}", responseNode.toPrettyString());

        if (responseNode.has("result") && responseNode.get("result").get("allow").asBoolean()) {
            return true;
        }

        return false;
    }

}
