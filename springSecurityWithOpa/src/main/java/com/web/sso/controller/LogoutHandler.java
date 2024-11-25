package com.web.sso.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LogoutHandler extends SecurityContextLogoutHandler {

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {

        super.logout(httpServletRequest, httpServletResponse, authentication);

        String logoutUrl = UriComponentsBuilder.fromHttpUrl("https://dev-ru75shw6v5e3ddlq.us.auth0.com/v2/logout").encode().toUriString();

        log.info("Attempting to redirect to: {}", logoutUrl);
        try {
            httpServletResponse.sendRedirect(logoutUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
