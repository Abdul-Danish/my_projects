package com.web.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.web.sso.dto.UserDto;
import com.web.sso.model.Roles;
import com.web.sso.service.Auth0ManagementService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Autowired
    private Auth0ManagementService managementService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    @ResponseBody
    public ResponseEntity<String> getUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("Management Token {}", managementService.getManagementApiToken());
        headers.set(AUTHORIZATION_HEADER, BEARER + managementService.getManagementApiToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange("https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/users", HttpMethod.GET, entity, String.class);
    }

    @PostMapping
    public ResponseEntity<String> createUsers(@RequestBody UserDto user) {
        String url = "https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION_HEADER, BEARER + managementService.getManagementApiToken());

        JSONObject requestBody = new JSONObject();
        requestBody.put("email", user.getEmail());
        requestBody.put("given_name", user.getName());
        requestBody.put("family_name", user.getFamily_name());
        requestBody.put("connection", "Username-Password-Authentication");
        requestBody.put("password", user.getPassword());

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        return restTemplate.postForEntity(url, entity, String.class);
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<String> assignRoles(@PathVariable String userId, @RequestBody Roles roles) {
        String url = String.format("https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/users/%s/roles", userId);
        String managementToken = managementService.getManagementApiToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION_HEADER, BEARER + managementToken);

        JSONObject requestBody = new JSONObject();
        requestBody.put("roles", roles.getRole());
        log.info("Assign Role Req Obj {}", requestBody);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @GetMapping("/{userId}/permissions")
    public ResponseEntity<String> getUserPermissions(@PathVariable String userId) {
        log.info("User Id {}", userId);
        String url = String.format("https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/users/%s/permissions", userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION_HEADER, BEARER + managementService.getManagementApiToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

}
