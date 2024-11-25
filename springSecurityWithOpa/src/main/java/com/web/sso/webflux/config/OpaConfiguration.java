//package com.web.sso.webflux.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//@EnableConfigurationProperties(OpaProperties.class)
//public class OpaConfiguration {
//
//    @Autowired
//    private OpaProperties opaProperties;
//
//    @Bean
//    public WebClient opaWebClient(WebClient.Builder builder) {
//        return builder.baseUrl(opaProperties.getEndpoint()).build();
//    }
//
//}
