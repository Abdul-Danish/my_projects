package com.web.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.web.sso.model.ResourceServer;
import com.web.sso.model.Scopes;
import com.web.sso.service.Auth0ManagementService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/api/v1/resource-servers")
public class ResourceServerController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Autowired
    private Auth0ManagementService managementService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<JsonNode> createResourceServer(@RequestBody ResourceServer resourceServer) throws Exception {
        String url = "https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/resource-servers";
        String managementToken = managementService.getManagementApiToken();
        log.info("Management Token {}", managementToken);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(AUTHORIZATION_HEADER, BEARER + managementToken);

            JSONObject requestBody = new JSONObject();
            requestBody.put("name", resourceServer.getName());
            requestBody.put("identifier", resourceServer.getIdentifier());
            requestBody.put("scopes", resourceServer.getScopes());
            log.info("Resource Server request Obj {}", requestBody);

            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);
            log.info("API Created Successfully!");
            enableRbac(response.getBody());
            return response;
        } catch (Exception e) {
            throw new Exception("Failed to Create Api");
        }
    }

    @PostMapping("/{resourceServerId}/scopes")
    public ResponseEntity<String> addScope(@PathVariable String resourceServerId, @RequestBody Scopes scopes) throws Exception {
        try {
            String url = String.format("https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/resource-servers/%s", resourceServerId);
            String managementToken = managementService.getManagementApiToken();

            log.info("Management Token {}", managementToken);

            HttpHeaders headers = new HttpHeaders();
            headers.set(AUTHORIZATION_HEADER, BEARER + managementToken);

            JSONObject requestBody = new JSONObject();
            requestBody.put("scopes", scopes.getScope());

            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
            return restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);
        } catch (Exception ex) {
            log.error("Error while adding Scope {}", ex.getMessage());
            throw new Exception("Failed to add Scope");
        }
    }

    public void enableRbac(JsonNode resourceServer) throws Exception {
        try {
            log.info("ID {}", resourceServer.get("identifier").asText());
            String url = String.format("https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/resource-servers/%s",
                resourceServer.get("identifier").asText());

            String managementToken = managementService.getManagementApiToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set(AUTHORIZATION_HEADER, BEARER + managementToken);

            JSONObject requestBody = new JSONObject();
            requestBody.put("enforce_policies", true);
            requestBody.put("token_dialect", "access_token_authz");

            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);
        } catch (Exception e) {
            throw new Exception("Failed to Enable RBAC settings");
        }
    }

}
