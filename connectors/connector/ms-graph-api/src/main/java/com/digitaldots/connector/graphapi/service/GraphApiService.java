/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.graphapi.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.graphapi.util.GraphApiHelper;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.kiota.RequestInformation;

import lombok.extern.slf4j.Slf4j;

@Connector(id = "MicrosoftGraph")
@Slf4j
public class GraphApiService {

    @Autowired
    private ObjectMapper mapper;

    @Connection
    public GraphApiHelper graphApiHelper;

    @Params(name = "pathParams")
    protected Map<String, String> pathParams;

    @Params(name = "path")
    protected String path;

    @Params(name = "queryParams")
    protected Map<String, String> queryParams;

    @Params(name = "requestBody")
    protected String requestBody;

    @Execute
    public JsonObject execute(ConnectorRequest<?> request) {
        UriBuilder uriBuilder = UriComponentsBuilder.fromPath(path);
        if (queryParams != null) {
            uriBuilder.queryParams(getParams(queryParams));
        }
        URI uri = uriBuilder.build(getPathVariables(request.getRequestParameters()));
        if (requestBody != null) {
            return sendRequest(uri.toString(), request.getRequestParameter("method"), requestBody);
        } else {
            return sendRequest(uri.toString(), request.getRequestParameter("method"), "");
        }
    }

    private JsonObject sendRequest(String uri, String method, String requestBody) {
        // CustomRequest<JsonElement> req = graphApiHelper.getGraphClient().customRequest(uri).buildRequest();
        // log.debug("URL {}", req.getRequestUrl().toString());
        log.debug("METHOD {}", method);
        JsonObject response = new JsonObject();
        try {
            switch (method) {
            case "GET": {
                // response = (JsonObject) req.get();
                RequestInformation getRequestInformation = graphApiHelper.getGraphClient().directoryObjects().toGetRequestInformation();
                response = (JsonObject) mapper.readValue(getRequestInformation.toString(), JsonObject.class);
                break;
            }
            case "POST": {
                // response = (JsonObject) req.post(mapper.readValue(requestBody, JsonObject.class));
                RequestInformation postRequestInformation = graphApiHelper.getGraphClient().directoryObjects()
                    .toPostRequestInformation(mapper.readValue(requestBody, DirectoryObject.class));
                response = (JsonObject) mapper.readValue(postRequestInformation.toString(), JsonObject.class);
                break;
            }
            /*
             * case "PUT": { // response = (JsonObject) req.put(mapper.readValue(requestBody, JsonObject.class)); break; } case "PATCH": {
             * // response = (JsonObject) req.patch(mapper.readValue(requestBody, JsonObject.class)); break; }
             */
            default:
                throw new IllegalArgumentException("Invalid Http Method is used");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new ConnectorException(e.getMessage());
        }
        response.remove("@odata.context");
        response.remove("@odata.count");
        response.remove("@odata.next");
        log.debug("RESPONSE {}", response);
        return response;
    }

    private static Map<String, ?> getPathVariables(Object uriPrams) {
        if (uriPrams instanceof Map) {
            return (Map<String, String>) uriPrams;
        }
        return new HashMap<>();
    }

    private static MultiValueMap<String, String> getParams(Map<String, String> param) {
        MultiValueMap<String, String> querieParams = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> paramEntity : param.entrySet()) {
            List<String> list = new ArrayList<>();
            list.add(paramEntity.getValue());
            querieParams.put(paramEntity.getKey(), list);
        }
        return querieParams;
    }

}
