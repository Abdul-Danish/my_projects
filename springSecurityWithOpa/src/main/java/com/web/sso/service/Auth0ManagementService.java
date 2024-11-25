package com.web.sso.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Auth0ManagementService {

    @Autowired
    private RestTemplate restTemplate;

    private String managementToken = null;

    public String getManagementApiToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        log.info("EXPIRED METHOD RESPONSE {}", isExpired());

        if (isExpired()) {
            log.info("MANAGEMENT TOKEN NULL OR EXPIRED");
            log.info("MANAGEMENT TOKEN {}", managementToken);

            JSONObject requestBody = new JSONObject();
            requestBody.put("client_id", "NBPy5M3GJN4gZkwFppW4F7NAnVYQVN1o");
            requestBody.put("client_secret", "hlAy5gtWXCZPwXH2kBMuyA7-fyf83dbSUgjrz2GDgpf0UBf5XYqS7rrwfKd4Tg3j");
            requestBody.put("audience", "https://dev-ru75shw6v5e3ddlq.us.auth0.com/api/v2/");
            requestBody.put("grant_type", "client_credentials");

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            @SuppressWarnings("unchecked")
            Map<String, Object> responseNode = restTemplate.postForObject("https://dev-ru75shw6v5e3ddlq.us.auth0.com/oauth/token", request,
                HashMap.class);

            if (responseNode != null) {
                managementToken = responseNode.get("access_token").toString();
                return managementToken;
            }
            return null;
        } else {
            log.info("ACCESS TOKEN NOT EXPIRED");
            return managementToken;
        }
    }

    public boolean isExpired() {
        if (managementToken != null) {
            DecodedJWT decodedJwt = JWT.decode(managementToken);
            return decodedJwt.getExpiresAt().before(new Date());
        }
        return true;
    }

}
