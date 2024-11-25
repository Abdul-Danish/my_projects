//package com.bank.controller;
//
//import java.io.IOException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Controller
//@Slf4j
//public class LogoutHandler extends SecurityContextLogoutHandler {
//
//    private final ClientRegistrationRepository clientRegistrationRepository;
//    
//    @Autowired
//    public LogoutHandler(ClientRegistrationRepository clientRegistrationRepository) {
//        this.clientRegistrationRepository = clientRegistrationRepository;
//    }
//    
//
//    @Override
//    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, 
//        Authentication authentication) {
//        
//        super.logout(httpServletRequest, httpServletResponse, authentication);
//        
////        String issuer = (String) getClientRegistration().getProviderDetails().getConfigurationMetadata().get("issuer");
////        String clientId = getClientRegistration().getClientId();
////        String returnTo = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
//        
//        String logoutUrl = UriComponentsBuilder
////            .fromHttpUrl(issuer + "v2/logout?client_id={clientId}&returnTo={returnTo}")
//            .fromHttpUrl("https://dev-ru75shw6v5e3ddlq.us.auth0.com/v2/logout")
//            .encode()
////            .buildAndExpand(clientId, returnTo)
//            .toUriString();
//        
//        
//        log.info("Attempting to redirect to: {}", logoutUrl);
//        try {
//            httpServletResponse.sendRedirect(logoutUrl);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    
//    private ClientRegistration getClientRegistration() {
//        return this.clientRegistrationRepository.findByRegistrationId("auth0");
//    }
//    
//    
//}
