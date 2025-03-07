//package com.bank.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
//import org.springframework.security.oauth2.core.OAuth2TokenValidator;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtDecoders;
//import org.springframework.security.oauth2.jwt.JwtValidators;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import com.bank.controller.LogoutHandler;
//import com.bank.validator.AudienceValidator;
//
//@EnableWebSecurity
//public class OAuthSecurityConfig {
//    
//    /*
//    private LogoutHandler logoutHandler;
//    
//    public OAuthSecurityConfig(LogoutHandler logoutHandler) {
//        this.logoutHandler = logoutHandler;
//    }
//    
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.oauth2Login()
//            .and().logout()
//            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//            .addLogoutHandler(logoutHandler)
//            .and().build();
//    }
//    */
//    
//    
//    @Value("${auth0.audience}")
//    private String audience;
//    
//    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
//    private String issuer;
//    
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeRequests()
//            .antMatchers("/api/public").permitAll()
//            .antMatchers("/api/test/**").authenticated()
//            .antMatchers("/api/private-scoped").hasAuthority("SCOPE_read:messages")
//            .and().cors()
//            .and().oauth2ResourceServer().jwt();
//        
//        return httpSecurity.build();
//    }
//    
//    @Bean
//    JwtDecoder jwtDecoder() {
//        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);
//        
//        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
//        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
//        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
//        
//        jwtDecoder.setJwtValidator(withAudience);
//        return jwtDecoder;
//    }
//}
