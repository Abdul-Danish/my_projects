/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.elasticsearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

//@Component
@Connector(id = "ElasticSearch")
@Slf4j
@Getter
public class ElasticSearchService {

    private static final String SUCCESS = "Success";

    private static final int BAD_REQUEST_CODE = 400;

    private static final int STATUS_CODE_500 = 500;

    private static final int STATUS_CODE_202 = 202;

    private static final String EXCEPTION_MESSAGE = "Exception while executing connector";

    private static final String MESSAGE = "message";

    private static final int SUCCESS_STATUS_CODE = 200;

    private static final String STATUS = "status";

    @Connection
    protected ElasticsearchClient client;

    @Params()
    protected String payload;

    @Params(name = "method")
    protected String method;

    @Params(name = "filter")
    protected JSONObject filter;

    @Params(name = "EsClusterName")
    protected String esClusterName;

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {
        JSONObject response = null;
        try {
            switch (method) {
            case "SELECT":
                response = retrieve();
                return response;
            case "INSERT":
                response = insert();
                return response;
            case "UPDATE":
                response = update();
                return response;
            case "DELETE":
                response = delete();
                return response;
            default: {
                response = sendException();
            }
            }

        } catch (RuntimeException e) {
            response = sendErrorResponse(e);
        }
        return response;
    }

    private JSONObject sendErrorResponse(RuntimeException e) {
        JSONObject response;
        log.error(EXCEPTION_MESSAGE, e);
        response = new JSONObject();
        response.put(STATUS, STATUS_CODE_500);
        response.put(MESSAGE, e.getMessage());
        response.put("data", new JSONArray());
        return response;
    }

    private JSONObject sendException() {
        JSONObject response = new JSONObject();
        response.put(STATUS, STATUS_CODE_202);
        response.put(MESSAGE, "Not Supported");
        response.put("data", new JSONArray());
        return response;
    }

    private JSONObject retrieve() {

        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();

        try {
            List<Query> queries = new ArrayList<>();
            if (filter != null) {
                Iterator<String> keys = filter.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = filter.get(key);
                    queries.add(Query.of(q -> q.match(t -> t.field(key).query(value.toString()))));
                }
            } else {
                queries.add(Query.of(q -> q.matchAll(QueryBuilders.matchAll().build())));
            }
            SearchResponse<JSONObject> response = client
                .search(s -> s.index(esClusterName).from(0).size(100).query(q -> q.bool(b -> b.must(queries))), JSONObject.class);
            for (Hit<JSONObject> hit : response.hits().hits()) {
                data.put(hit);
            }
            result.put(STATUS, SUCCESS_STATUS_CODE);
            result.put("data", data);
        } catch (IOException e) {
            log.error(EXCEPTION_MESSAGE, e);
            sendErrorResponse(result, e);
        }
        return result;
    }

    private void sendErrorResponse(JSONObject response, IOException e) {
        response.put(STATUS, BAD_REQUEST_CODE);
        response.put(MESSAGE, e.getMessage());
        response.put("data", new JSONArray());
    }

    private JSONObject insert() {

        JSONObject response = new JSONObject();
        try {
            client.index(s -> s.index(esClusterName).document(Document.parse(payload)));
            response.put(STATUS, SUCCESS_STATUS_CODE);
            response.put(MESSAGE, SUCCESS);
            response.put("data", new JSONArray());
        } catch (IOException e) {
            log.error(EXCEPTION_MESSAGE, e);
            sendErrorResponse(response, e);
        }
        return response;
    }

    private JSONObject update() {

        JSONObject response = new JSONObject();
        try {
            client.update(s -> s.index(esClusterName).id(filter.getString("id")).doc(Document.parse(payload)), Object.class);
            response.put(STATUS, SUCCESS_STATUS_CODE);
            response.put(MESSAGE, SUCCESS);
            response.put("data", new JSONArray());
        } catch (IOException e) {
            log.error(EXCEPTION_MESSAGE, e);
            sendErrorResponse(response, e);
        }
        return response;
    }

    private JSONObject delete() {

        JSONObject response = new JSONObject();
        try {
            client.delete(s -> s.index(esClusterName).id(filter.getString("id")));
            response.put(STATUS, SUCCESS_STATUS_CODE);
            response.put(MESSAGE, SUCCESS);
            response.put("data", new JSONArray());
        } catch (IOException e) {
            log.error(EXCEPTION_MESSAGE, e);
            sendErrorResponse(response, e);
        }
        return response;
    }
}
