package com.oauth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
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
