/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sql.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Connector(id = "SQL")
@Slf4j
public class SqlService {

    private static final int SUCCESS_STATUS_CODE = 200;
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String DATA = "data";
    private static final int QUERY_LIMIT = 100;

    private enum methodEnum {
        SELECT, INSERT, UPDATE, DELETE, BATCH
    }

    @Connection
    public NamedParameterJdbcTemplate jdbcTemplate;

    @Params(name = "query")
    public String query;

    @Params(name = "params")
    public JsonNode params;

    @Params(name = "method")
    public String method;

    @Execute
    public JSONObject execute(ConnectorRequest<?> request) {
        ObjectMapper mapper = new ObjectMapper();
        JSONObject response = null;
        if (methodEnum.BATCH.name().equals(method)) {
            response = executeBatch(mapper);
        } else if (methodEnum.SELECT.name().equals(method)) {
            response = query(mapper);
        } else if (methodEnum.INSERT.name().equals(method)) {
            response = insertUpdateOrDelete(mapper);
        } else if (methodEnum.UPDATE.name().equals(method)) {
            response = insertUpdateOrDelete(mapper);
        } else if (methodEnum.DELETE.name().equals(method)) {
            response = insertUpdateOrDelete(mapper);
        } else {
            throw new ConnectorException("Method doesn't match any available operations");
        }
        return response;
    }

    private JSONObject query(ObjectMapper mapper) {
        JSONObject response = new JSONObject();
        JSONObject paramsNode = new JSONObject();
        if (Objects.nonNull(params)) {
            try {
                paramsNode = new JSONObject(mapper.writeValueAsString(params));
            } catch (JsonProcessingException | JSONException e) {
                log.error("Exception occured while querying", e);
            }
        }
        log.debug("Query is {} ", query);
        log.debug("paramsNode {}", paramsNode);
        SqlParameterSource namedParameters = new MapSqlParameterSource(paramsNode.toMap());
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query, namedParameters);
        result = result.size() > QUERY_LIMIT ? result.subList(0, QUERY_LIMIT) : result;
        response.put(STATUS, SUCCESS_STATUS_CODE);
        response.put(MESSAGE, HttpStatus.ACCEPTED);
        response.put(DATA, result);
        log.debug("response is : {}" + response.toString());
        return response;
    }

    private JSONObject executeBatch(ObjectMapper mapper) {
        JSONObject response = new JSONObject();
        JSONArray paramsList = new JSONArray();
        if (Objects.nonNull(params)) {
            try {
                paramsList = new JSONArray(mapper.writeValueAsString(params));
            } catch (JsonProcessingException | JSONException e) {
                log.error("Exception occured while querying batch", e);
            }
        }
        Map<String, Object>[] queryParams = new HashMap[paramsList.length()];
        for (int i = 0; i < paramsList.length(); i++) {
            queryParams[i] = mapper.convertValue(paramsList.get(i).toString(), JSONObject.class).toMap();
        }
        int[] result = jdbcTemplate.batchUpdate(query, queryParams);
        response.put(STATUS, SUCCESS_STATUS_CODE);
        response.put(MESSAGE, HttpStatus.ACCEPTED);
        response.put(DATA, result);
        return response;
    }

    private JSONObject insertUpdateOrDelete(ObjectMapper mapper) {
        JSONObject response = new JSONObject();
        JSONObject paramsNode = new JSONObject();
        if (Objects.nonNull(params)) {
            try {
                paramsNode = new JSONObject(mapper.writeValueAsString(params));
            } catch (JsonProcessingException | JSONException e) {
                log.error("Exception occured while inseting batch", e);
            }
        }
        SqlParameterSource namedParameters = new MapSqlParameterSource(paramsNode.toMap());
        int inserts = jdbcTemplate.update(query, namedParameters);
        response.put(STATUS, SUCCESS_STATUS_CODE);
        response.put(MESSAGE, HttpStatus.ACCEPTED);
        response.put(DATA, inserts);
        return response;
    }

}
