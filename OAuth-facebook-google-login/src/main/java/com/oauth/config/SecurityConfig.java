package com.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		HttpSecurity http = httpSecurity.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/public").permitAll().anyRequest().authenticated())
				.formLogin(form -> form.defaultSuccessUrl("/home")).httpBasic(Customizer.withDefaults())
				.oauth2Login(oauth -> {
					oauth.defaultSuccessUrl("/home").failureUrl("error");
				});

		return http.build();
	}

}
