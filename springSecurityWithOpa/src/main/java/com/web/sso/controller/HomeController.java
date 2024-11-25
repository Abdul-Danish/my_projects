package com.web.sso.controller;

import java.io.IOException;
import java.rmi.AccessException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.auth0.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.auth0.client-secret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${com.auth0.domain}")
    private String domain;

    @Value("${spring.security.oauth2.client.registration.auth0.scope}")
    private String scope;

    @GetMapping("/")
    @ResponseBody
    public String home(Model model, @AuthenticationPrincipal OidcUser principle, Authentication authentication) throws AccessException {
//        log.info("Inside Home Controller");
//
//        if (principle != null) {
//            model.addAttribute("profile", principle.getClaims());
//        }
//
//        return "index.html";
        return "<h1>Home Page</h1>";
    }

    @GetMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("LOGIN URL CALLED");
        String callbackUrl = "http://localhost:8080/callback";
        String url = "https://" + domain + "/authorize?redirect_uri=" + callbackUrl + "&client_id=" + clientId
            + "&scope=openid%20profile%20email&response_type=code&state=1234ABC&audience=" + audience;
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, HttpServletRequest httpServletRequest, HttpServletResponse response)
        throws IOException {
        log.info("CALLBACK URL CALLED");
        log.info("Code {}", code);

        String url = "https://dev-ru75shw6v5e3ddlq.us.auth0.com/oauth/token";
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("code", code);
        body.put("redirect_uri", "http://localhost:8080/login/oauth2/code/auth0");

        HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(body);
        JsonNode responseNode = restTemplate.postForObject(url, entity, JsonNode.class);

        if (responseNode != null) {
            log.info("Response Node {}", responseNode.toPrettyString());

            DecodedJWT jwt = JWT.decode(responseNode.get("id_token").toString());
            Authentication authenticationToken = new TestingAuthenticationToken(jwt.getSubject(),
                responseNode.get("access_token").toString());

            authenticationToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            response.sendRedirect("/");
        }

    }

}
