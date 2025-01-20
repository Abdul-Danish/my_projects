/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.script.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.script.exception.ScriptExecutionException;
import com.digitaldots.connector.script.service.ScriptExecutionHelper;
import com.digitaldots.connector.script.service.ScriptHelperRequest;
import com.google.gson.Gson;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("restriction")
@Component("Default")
@Slf4j
public class DefaultScriptExecutionHelper implements ScriptExecutionHelper {

    private static final String RESPONSE = "response";

    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();

    private static final Map<String, ScriptEngineFactory> ENGINE_LIST = new HashMap<>();

    @PostConstruct
    public void getEngines() {
        List<ScriptEngineFactory> factories = MANAGER.getEngineFactories();
        try {
            log.info("Loading Script Engines");
            for (ScriptEngineFactory factory : factories) {
                log.info(
                    String.format(" Engine Name: %s Version: %s :: Language: %s Version: %s :: Short Names: %s", factory.getEngineName(),
                        factory.getEngineVersion(), factory.getLanguageName(), factory.getLanguageVersion(), factory.getNames()));
                List<String> shortNames = factory.getNames();
                for (String shortName : shortNames) {
                    ENGINE_LIST.put(shortName, factory);
                }
            }
        } catch (RuntimeException e) {
            log.error("Exception Occured while loading script engines ", e);
            e.printStackTrace();
        }
    }

    public JSONObject execute(ScriptHelperRequest request) {
        ScriptEngine engine = ENGINE_LIST.get(request.getScriptFormat()).getScriptEngine();
        ScriptContext context = engine.getContext();
        context.setAttribute("payload", request.getPayload(), ScriptContext.ENGINE_SCOPE);
        context.setAttribute("vars", request.getVars(), ScriptContext.ENGINE_SCOPE);
        try {
            Object response = engine.eval(request.getScript(), context);
            JSONObject jsonResponse = new JSONObject();

            if (Objects.isNull(response)) {
                String pld = request.getScript();
                for (int i = pld.length() - 1; i >= 0; i--) {
                    final int ASCII_CODE = 10;
                    if (ASCII_CODE == (int) pld.charAt(i)) {
                        String key;
                        char semiColon = pld.charAt(pld.length() - 1);
                        final int ASCII_CODE_SEMICOLON = 59;
                        if (ASCII_CODE_SEMICOLON == (int) semiColon) {
                            key = pld.substring(i + 1, pld.length() - 1);
                        } else {
                            key = pld.substring(i + 1, pld.length());
                        }
                        response = engine.get(key);
                        break;
                    }
                }
            }
            if (Objects.nonNull(response)) {
                log.trace("{} eval {} :: {}", request.getScriptFormat(), response.getClass().getName(), response);
            }
            jsonResponse = buildResponse(response, jsonResponse);

            log.debug("{} return {} :: {}", request.getScriptFormat(), jsonResponse.getClass().getName(), jsonResponse);
            return jsonResponse;
        } catch (JSONException | ScriptException e) {
            log.error("exception while executing default script engine, {}", e);
            throw new ScriptExecutionException("Exception while executing " + request.getScriptFormat(), e);
        } finally {
            context.removeAttribute("payload", ScriptContext.ENGINE_SCOPE);
            context.removeAttribute("vars", ScriptContext.ENGINE_SCOPE);
        }
    }

    private JSONObject buildResponse(Object response, JSONObject jsonResponse) {
        Gson gson = new Gson();
        String json = null;
        if (Objects.isNull(response)) {
            json = "{}";
        } else if (response instanceof String) {
            json = (String) response;
        } else if (response instanceof ScriptObjectMirror) {
            json = serializeScriptObjectMirror(ScriptObjectMirror.wrapAsJSONCompatible(response, null));
        } else {
            json = gson.toJson(response);
        }
        log.trace("build {} :: {}", json.getClass().getName(), json);

        if (json.startsWith("{")) {
            return new JSONObject(json);
        } else if (json.startsWith("[")) {
            jsonResponse.put(RESPONSE, new JSONArray(json));
        } else {
            jsonResponse.put(RESPONSE, json);
        }

        return jsonResponse;
    }

    private String serializeScriptObjectMirror(Object obj) {
        StringBuilder ret = new StringBuilder();
        if (obj instanceof ScriptObjectMirror) {
            ScriptObjectMirror om = (ScriptObjectMirror) obj;
            if (om.isFunction()) {
                ret.append(om.toString());
            } else if (om.isArray()) {
                ret.append("[");
                List<Object> omList = om.values().stream().collect(Collectors.toCollection(ArrayList::new));
                for (int x = 0; x < omList.size(); x++) {
                    ret.append(serializeScriptObjectMirror(omList.get(x)));
                    if ((x + 1) < omList.size()) {
                        ret.append(",");
                    }
                }
                ret.append("]");
            } else if (om.toString().indexOf("global") > -1) {
                Iterator<Map.Entry<String, Object>> it = om.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Object> entry = it.next();
                    ret.append("var " + entry.getKey() + "=" + serializeScriptObjectMirror(entry.getValue()) + ";\n");
                }
            } else {
                ret.append("{");
                Iterator<Map.Entry<String, Object>> it = om.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Object> entry = it.next();
                    ret.append("\"" + entry.getKey() + "\":" + serializeScriptObjectMirror(entry.getValue()));
                    if (it.hasNext()) {
                        ret.append(",");
                    }
                }
                ret.append("}");
            }
        } else if (obj instanceof org.openjdk.nashorn.internal.runtime.JSONListAdapter) {
            org.openjdk.nashorn.internal.runtime.JSONListAdapter om = (org.openjdk.nashorn.internal.runtime.JSONListAdapter) obj;
            ret.append("[");
            List<Object> omList = om.values().stream().collect(Collectors.toCollection(ArrayList::new));
            for (int x = 0; x < omList.size(); x++) {
                ret.append(serializeScriptObjectMirror(omList.get(x)));
                if ((x + 1) < omList.size()) {
                    ret.append(",");
                }
            }
            ret.append("]");
        } else if (obj instanceof java.util.Map) {
            Gson gson = new Gson();
            ret.append(gson.toJson(obj));
        } else if (obj instanceof String) {
            ret.append("\"" + obj + "\"");
        } else {
            ret.append(obj);
        }
        return ret.toString();
    }

    @Override
    public String type() {
        return "default";
    }
}
