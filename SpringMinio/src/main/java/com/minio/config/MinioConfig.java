package com.minio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    @Value("${minio.access.key}")
    private String minioAccessKey;

    @Value("${minio.secret.key}")
    private String minioSecretKey;

    @Value("${minio.url}")
    private String minioUrl;

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder().endpoint(minioUrl).credentials(minioAccessKey, minioSecretKey).build();
    }

}
