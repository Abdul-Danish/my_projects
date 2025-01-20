/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.scraper;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Connector(id = "SCRAPERS")
public class ScraperService {

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Connection
    public ScraperHelper scraperHelper;

    @Params(name = "path")
    protected String path;

    @Params(name = "uriParams")
    protected Map<String, String> uriParams;

    @Params(name = "jobId")
    protected String jobId;

    @Params(name = "method")
    protected String method;

    @Params(name = "solutionId")
    protected String solutionId;

    @Params()
    protected JSONObject payload;

    @Params(name = "headers")
    protected Map<String, String> headers;

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(scraperHelper.getBaseUrl());
        ResponseEntity<String> restResponse = null;
        JSONObject response = new JSONObject();
        Object auth = null;
        URI url = null;
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
            && SecurityContextHolder.getContext().getAuthentication().getCredentials() != null) {
            auth = SecurityContextHolder.getContext().getAuthentication().getCredentials();
            log.debug("auth token is {}", auth);
        }

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (Objects.nonNull(headers)) {
                log.debug("Invoking scraper headers is {} ", headers);
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpHeaders.add(header.getKey(), header.getValue());
                }
            }
            httpHeaders.addIfAbsent(HttpHeaders.AUTHORIZATION, AssetConstants.BEARER + auth);
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            uriBuilder.path(path);
            Map<String, String> pathParams = new HashMap<>();
            if (uriParams != null) {
                pathParams = uriParams;
            }
            pathParams.put(AssetConstants.PROJECT, scraperHelper.getProject());
            pathParams.put(AssetConstants.SPIDER, scraperHelper.getSpider());
            pathParams.putIfAbsent(AssetConstants.JOB_ID, jobId);
            pathParams.putIfAbsent(AssetConstants.SOLUTION_ID, solutionId);

            log.debug("Path Params are, {}", pathParams.entrySet());

            if (AssetConstants.JOB_RESULT.equals(method) || AssetConstants.JOB_STATUS.equals(method)) {
                url = uriBuilder.build(pathParams);
                log.debug("Invoking scrapers method : {} and url : {} ", method, url.toString());

                RequestEntity<?> requestEntity = RequestEntity.get(url).accept(MediaType.APPLICATION_JSON).headers(httpHeaders).build();
                restResponse = sendRequest(requestEntity);
                log.debug(restResponse.getBody());
                response.put(AssetConstants.BODY, new JSONObject(restResponse.getBody()));

            } else {
                url = uriBuilder.build(pathParams);
                log.debug("Invoking scrapers method : {} and url : {} ", method, url.toString());

                Map<String, Object> body = new HashMap<>();
                if (this.payload != null) {
                    for (Map.Entry<String, Object> entity : payload.toMap().entrySet()) {
                        body.put(entity.getKey(), entity.getValue());
                    }
                }
                for (Map.Entry<String, Object> entity : scraperHelper.getConfiguration().entrySet()) {
                    body.putIfAbsent(entity.getKey(), entity.getValue());
                }

                log.debug("Invoking scraper payload is {} ", payload);
                RequestEntity<?> requestEntity = RequestEntity.post(url).contentType(MediaType.APPLICATION_JSON).headers(httpHeaders)
                    .body(body);

                restResponse = sendRequest(requestEntity);
                @SuppressWarnings("unchecked")
                Map<String, String> responseBody = mapper.readValue(restResponse.getBody(), Map.class);
                response.put(AssetConstants.BODY, responseBody);
            }
            response.put(AssetConstants.STATUS, restResponse.getStatusCodeValue());
        } catch (JsonProcessingException e) {
            throw new ConnectorException("Scraper Execution Error {}", e);
        }
        return response;
    }

    private ResponseEntity<String> sendRequest(RequestEntity<?> requestEntity) {
        ResponseEntity<String> response;
        response = restTemplate.exchange(requestEntity, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new ConnectorException("Scraper Execution Error {}");
        }
        return response;
    }
}
