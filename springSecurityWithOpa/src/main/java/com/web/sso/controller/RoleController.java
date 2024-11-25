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

import com.nimbusds.jose.shaded.json.JSONObject;
import com.web.sso.model.Permissions;
import com.web.sso.model.Role;
import com.web.sso.service.Auth0ManagementService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/api/v1/roles")
public class RoleController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Autowired
    private Auth0ManagementService managementService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<String> createRole(@RequestBody Role role) {
        String url = "https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/roles";
        String managementToken = managementService.getManagementApiToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, BEARER + managementToken);

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", role.getName());
        requestBody.put("description", role.getDescription());

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<String> assignPermissions(@PathVariable String roleId, @RequestBody Permissions permissions) {
        String url = String.format("https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/roles/%s/permissions", roleId);
        String managementToken = managementService.getManagementApiToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, BEARER + managementToken);

        JSONObject requestBody = new JSONObject();
        requestBody.put("permissions", permissions.getPermission());

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

}
