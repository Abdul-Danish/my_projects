package com.web.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SingleSignOnDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingleSignOnDemoApplication.class, args);
    }

    @Bean
    public String string() {
        return new String();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

}
