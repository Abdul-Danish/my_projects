package com.web.sso.mvc.config;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.web.sso.controller.LogoutHandler;
import com.web.sso.validator.AudienceValidator;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private LogoutHandler logoutHandler;

    public WebSecurityConfiguration(LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    @Value("${opa.endpoint}")
    private String opaAuthUrl;

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Value("${com.auth0.domain}")
    private String domain;

    @Value("${spring.security.oauth2.client.registration.auth0.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.auth0.client-secret}")
    private String clientSecret;

    /*
     * @Override public void configure(HttpSecurity httpSecurity) throws Exception {
     * httpSecurity.csrf().disable().oauth2Login().and().authorizeRequests() // .accessDecisionManager(accessDecisionManager())
     * .antMatchers(HttpMethod.GET, "/user").permitAll().anyRequest().authenticated().and().logout() .logoutRequestMatcher(new
     * AntPathRequestMatcher("/logout")).addLogoutHandler(logoutHandler); // .and().authorizeHttpRequests() // .antMatchers(HttpMethod.GET,
     * "/admin"); }
     */

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable().oauth2Login().loginPage("/login").and()
//            .formLogin().loginPage("/login").and()
            .authorizeRequests().antMatchers(HttpMethod.GET, "/admin/**", "/get-solutions/**", "/api/v1/**").authenticated()
            .accessDecisionManager(accessDecisionManager()).and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .addLogoutHandler(logoutHandler).and().oauth2ResourceServer().jwt().decoder(jwtDecoder())
            .jwtAuthenticationConverter(jwtAuthenticationConverter());
    }

    @Bean
    public UnanimousBased accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(new OpaVoter(opaAuthUrl));
        return new UnanimousBased(decisionVoters);
    }

//    @Bean
//    public AuthenticationController authenticationController() {
//        JwkProvider jwkProvider = new JwkProviderBuilder(domain).build();
//        return AuthenticationController.newBuilder(domain, clientId, clientSecret).withJwkProvider(jwkProvider).build();
//    }

    public String getContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }

    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("permissions");
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
