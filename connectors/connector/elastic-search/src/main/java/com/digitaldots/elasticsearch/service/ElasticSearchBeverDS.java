/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.elasticsearch.service;

import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.cache.Store;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Component
public class ElasticSearchBeverDS implements Store<ElasticsearchClient, ElasticsearchClient> {

    @Override
    public ElasticsearchClient getDataSource(Map<String, Object> configProperties) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (configProperties.get("user") != null) {
            credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(configProperties.get("user").toString(), configProperties.get("password").toString()));
        }
        HttpClientConfigCallback clientConfig = httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        RestClientBuilder builder = RestClient
            .builder(new HttpHost(configProperties.get("EsHost").toString(), Integer.parseInt(configProperties.get("EsPort").toString())))
            .setHttpClientConfigCallback(clientConfig);
        ElasticsearchTransport transport = new RestClientTransport(builder.build(), new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    @Override
    public String getDBTye() {
        return "ElasticSearch";
    }

    @Override
    public ElasticsearchClient getConnection(ElasticsearchClient client) {
        return client;
    }

}
