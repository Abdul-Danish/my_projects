package com.web.sso.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/api/v1/solutions")
    public String solutionApi() {
        return "solution api access granted";
    }

    @GetMapping("/admin/{id}")
    public String authTest(@PathVariable(name = "id") String id) {
        return "Access Allowed: User id - " + id;
    }

    @GetMapping("/admin")
    public String authTest2() {
        return "admin api";
    }

    @GetMapping("/get-messages")
    public String authTest3() {
        return "<h1>Successfull! ^ ^</h1>";
    }

    @GetMapping("/user")
    public String publicEndpoint(Authentication authentication) {
//        TestingAuthenticationToken token = (TestingAuthenticationToken) authentication;
//        log.info("Home Credentials: {}", token.getCredentials().toString());
        return "public data";
    }

}
