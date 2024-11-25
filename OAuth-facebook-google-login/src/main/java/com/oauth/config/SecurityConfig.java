package com.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.oauth.handler.LogoutHandler;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
//    @Autowired
//    private LogoutHandler logoutHandler;
    
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        
        httpSecurity.csrf().disable().formLogin().and().httpBasic().and()
        .authorizeRequests()
        .antMatchers("/public").permitAll()
        .anyRequest().authenticated().and()
        .oauth2Login().defaultSuccessUrl("/home").failureUrl("/error");
//        .logout();
        
    }

}
