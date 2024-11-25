package com.digitaldots.adminserver.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
// import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AdminServerConfig {

    // @LoadBalanced
    @Primary
    @Bean
    public RestTemplate getRestTempalte() {
        return new RestTemplateBuilder().build();
    }

}