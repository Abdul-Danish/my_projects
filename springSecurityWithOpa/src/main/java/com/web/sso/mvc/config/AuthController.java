//package com.web.sso.mvc.config;
//
//import java.io.IOException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.TestingAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import com.auth0.AuthenticationController;
//import com.auth0.IdentityVerificationException;
//import com.auth0.Tokens;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.interfaces.DecodedJWT;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Controller
//@Slf4j
//public class AuthController {
//
//    @Value("${auth0.audience}")
//    private String audience;
//
//    @Autowired
//    private WebSecurityConfiguration configuration;
//
//    @Autowired
//    private AuthenticationController authenticationController;
//
//    @GetMapping("/login")
//    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String redirectUri = configuration.getContextPath(request) + "/callback";
////        String redirectUri = configuration.getContextPath(request) + "/login/oauth2/code/auth0";
//        String authorizeUrl = authenticationController.buildAuthorizeUrl(request, response, redirectUri).withScope("openid profile email")
//            .build();
//        log.info("Authorize Url: {}", authorizeUrl.concat(String.format("&audience=%s", audience)));
//        log.info("Redirecting to callback url");
//        response.sendRedirect(authorizeUrl.concat(String.format("&audience=%s", audience)));
//    }
//
//    @GetMapping("/callback")
//    public void callback(HttpServletRequest request, HttpServletResponse response) throws IdentityVerificationException, IOException {
//        log.info("In CallBack Controller");
//
//        Tokens tokens = authenticationController.handle(request, response);
//
//        DecodedJWT jwt = JWT.decode(tokens.getIdToken());
//        TestingAuthenticationToken authToken2 = new TestingAuthenticationToken(jwt.getSubject(), jwt.getToken());
//        authToken2.setAuthenticated(true);
//
//        SecurityContextHolder.getContext().setAuthentication(authToken2);
//    }
//
//}
