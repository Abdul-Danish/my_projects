/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.elasticsearch.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.digitaldots.connector.spi.ConnectorRequest;

public class ElasticSearchServiceTest {

    @Autowired
    static ConnectorRequest<?> connectorRequest;

    private RestClientBuilder getBuilder() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "password"));

        HttpClientConfigCallback clientConfig = new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        };

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200)).setHttpClientConfigCallback(clientConfig);
        return builder;
    }

    @Test
    public void fetch01() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "SELECT";

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void fetch02() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "SELECT";
        elasticSearchService.filter = new JSONObject(
            "{\"query\":{\"multi_match\":{\"query\":\"Neha Verma\",\"type\":\"best_fields\",\"fields\":[ \"firstName\", \"lastName\" ],\"operator\":\"and\"}}}");

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void fetch03() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "SELECT";
        elasticSearchService.filter = new JSONObject(
            "{\"from\":0,\"size\":1000,\"query\":{\"filter\":{\"not\":{\"filter\":{\"term\":{\"firstNama\":\"Gili\"}}}}}}");

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void fetch04() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "SELECT";
        elasticSearchService.filter = new JSONObject("{\"query\": {\"regexp\":{\"firstName\": \"gili.*\"}}}");

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void fetch05() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "SELECT";
        elasticSearchService.filter = new JSONObject("{\"query\": {\"bool\": {\"filter\": [{\"range\": {\"age\": {\"gt\": 23}}}]}}}");

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void insert01() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "INSERT";
        elasticSearchService.payload = "{\"title\": \"Spring + Spring Data + ElasticSearch\",\"category\":\"Spring Boot\",\"published_date\":\"23-MAR-2017\",\"author\":\"Gitnajli Verma\"}";

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void insert02() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "INSERT";
        elasticSearchService.payload = "{\"title\": \"Spring + Spring Data + ElasticSearch\",\"category\":\"Spring Boot\",\"published_date\":\"23-MAR-2017\",\"author\":\"Gitnajli Verma\"},{\"title\": \"Spring + Spring Data + ElasticSearch\",\"category\":\"Spring Boot\",\"published_date\":\"23-MAR-2017\",\"author\":\"Gitnajli Verma\"}";

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void update01() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "UPDATE";
        elasticSearchService.filter = new JSONObject("{ \"id\" : \"hyA52nUBY1_6QEY5gV7c\" }");
        elasticSearchService.payload = "{ \"title\": \"Spring + Spring Data + ElasticSearch\", \"category\":\"Spring Boot\",\"published_date\":\"23-MAR-2017\",\"author\":\"Varsha Verma\"}";

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void update02() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "UPDATE";
        elasticSearchService.filter = new JSONObject("{ \"id\" : \"hyA52nUBY1_6QEY5gV7c\" }");
        elasticSearchService.payload = "{\"title\": \"Spring + Spring Data + ElasticSearch\",\"category\":\"Spring Boot\",\"published_date\":\"23-MAR-2017\",\"author\":\"Gitnajli Verma\"},{\"title\": \"Spring + Spring Data + ElasticSearch\",\"category\":\"Spring Boot\",\"published_date\":\"23-MAR-2017\",\"author\":\"Gitnajli Verma\"}";

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

    @Test
    public void delete01() {
        ElasticSearchService elasticSearchService = new ElasticSearchService();
        elasticSearchService.esClusterName = "elastic-search";
        elasticSearchService.method = "DELETE";
        elasticSearchService.filter = new JSONObject("{ \"id\" : \"1111\" }");

        JSONObject response = elasticSearchService.execute(connectorRequest);
        assertThat(response).isNotNull();
        System.out.println("\n" + response);
    }

}
