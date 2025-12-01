package com.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootApplication
@RestController
@OAuth2ClientSecurityMarker
public class OAuthFacebookGoogleLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(OAuthFacebookGoogleLoginApplication.class, args);
	}
	
	@GetMapping("/public")
    public String publicDef() {
        return "Public";
    }
	
	@GetMapping("/home")
	public String def() {
	    System.out.println("Context: " + SecurityContextHolder.getContext().getAuthentication());
	    return "Home";
    }
	
//	@GetMapping("/oauth/logout")
//    public ResponseEntity<String> revoke(HttpServletRequest request, HttpServletResponse response) {
//        System.out.println("##################");
//        try {
//            String authorization = request.getHeader("Authorization");
//            if (authorization != null && authorization.contains("Bearer")) {
//                String tokenValue = authorization.replace("Bearer", "").trim();
//
//                OAuth2AccessToken accessToken = request.getHeader("authorization");
//                tokenStore.removeAccessToken(accessToken);
//
//                //OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(tokenValue);
//                OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
//                tokenStore.removeRefreshToken(refreshToken);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Invalid access token");
//        }
//
//        return ResponseEntity.ok().body("Access token invalidated successfully");
//    }
    
	@GetMapping("/logout")
    public String revoke(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("##################");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null){
            System.out.println("AUTH: " + auth);
            new SecurityContextLogoutHandler().logout(request, response, auth);
//        }
        return "Logged out";
    }
	
//	@GetMapping("/api/logout")
//	public String logout(HttpServletRequest request) {
//	    SecurityContextHolder.getContext().setAuthentication(null);
//	    return "logged out";
//	}

}
