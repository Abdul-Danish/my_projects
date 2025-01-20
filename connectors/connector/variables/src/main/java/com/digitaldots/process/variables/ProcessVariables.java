package com.digitaldots.process.variables;

/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.script.ScriptExecutionHelperFactory;
import com.digitaldots.connector.script.service.ScriptHelperRequest;
import com.digitaldots.platform.process.entity.Message;
import com.digitaldots.process.constant.ProcessConstants;
import com.digitaldots.process.elements.Parameter;
import com.digitaldots.process.elements.ParameterType;
import com.digitaldots.process.exception.Errors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProcessVariables implements Variables {

    private static final String RESPONSE = "response";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AutowireCapableBeanFactory appContext;

    @Autowired
    private SpelExpressionParser expressionParser;

    @Autowired
    private ScriptExecutionHelperFactory scriptFactory;

    private final ParserContext templateParserContext = new TemplateParserContext("${", "}");

    private Object evalMapExpression(Message message, Map<String, String> map, ParserContext context) {
        Map<String, Object> resp = new HashMap<>();
        if (Objects.nonNull(map)) {
            for (Map.Entry<String, String> entity : map.entrySet()) {
                resp.put(entity.getKey(), this.evaluateExpression(message, entity.getValue(), context));
            }
        }
        return resp;
    }

    protected Object evaluateExpression(Object message, String expression, ParserContext parserContext) {
        StandardEvaluationContext evaluationContext = null;
        try {
            if (message != null) {
                evaluationContext = (StandardEvaluationContext) appContext.getBean("evaluationContext");
                evaluationContext.setRootObject(message);
                Expression parseExpression = expressionParser.parseExpression(expression, parserContext);
                log.trace("evaluating expression {} ", expression);
                return parseExpression.getValue(evaluationContext);
            } else {
                return expressionParser.parseExpression(expression);
            }
        } finally {
            if (evaluationContext != null) {
                appContext.destroyBean(evaluationContext);
            }
        }
    }

    private Object evaluateList(List<String> listExpr, Message message, ParserContext parserContext) {
        List<Object> list = new ArrayList<>();
        for (String expr : listExpr) {
            list.add(evaluateExpression(message, expr, parserContext));
        }
        return list;
    }

    public Object evaluateScript(String script, String scriptFormat, Message event) {
        log.trace("Evaluating script {} with format {}", script, scriptFormat);
        ScriptHelperRequest scriptHelperRequest = ScriptHelperRequest.builder().payload(event.getPayload()).vars(event.getVars())
            .scriptFormat(scriptFormat).script(script).build();
        JSONObject scriptResponse = scriptFactory.execute(scriptHelperRequest);
        Map<String, Object> jsonMap = null;
        try {
            jsonMap = objectMapper.readValue(scriptResponse.toString(), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            jsonMap = new HashMap<>();
        }
        if (scriptResponse.has(RESPONSE)) {
            return objectMapper.convertValue(jsonMap.get(RESPONSE), Object.class);
        }
        return objectMapper.convertValue(jsonMap, Object.class);
    }

    @Override
    public <T> T getForObject(String expression, Class<T> responseType, Object message) {
        try {
            Object evaluateExpression = evaluateExpression(message, expression, templateParserContext);
            return responseType.cast(evaluateExpression);
        } catch (RuntimeException e) {
            log.warn("Exception occured while executing the expression {} , null value will be returned ", expression, e);
            return null;
        }
    }

    @Override
    public Message process(List<Parameter> ioParams, Message event) {
        if (Objects.isNull(ioParams) || ioParams.isEmpty()) {
            return event;
        }
        try {
            Message message = Message.builder().context(event.getContext()).env(event.getEnv()).build();
            message.getVars().putAll(event.getVars());
            for (Parameter parameter : ioParams) {
                if (ParameterType.TEXT.name().equalsIgnoreCase(parameter.getType())) {
                    message.getVars().put(parameter.getName(), evaluateExpression(event, parameter.getValue(), templateParserContext));
                } else if (ParameterType.MAP.name().equalsIgnoreCase(parameter.getType())) {
                    message.getVars().put(parameter.getName(), evalMapExpression(event, parameter.getMap(), templateParserContext));
                } else if (ParameterType.INLINE.name().equalsIgnoreCase(parameter.getType())) {
                    message.getVars().put(parameter.getName(), evaluateScript(parameter.getValue(), parameter.getScriptFormat(), event));
                } else if (ParameterType.LIST.name().equalsIgnoreCase(parameter.getType())) {
                    message.getVars().put(parameter.getName(), evaluateList(parameter.getList(), event, templateParserContext));
                } else {
                    log.debug("No matching Param Type found for processing, expression is ignored.");
                }
            }
            Object payload = message.getVars().get("payload");
            if (payload != null) {
                message.setPayload(payload);
                message.getVars().remove(ProcessConstants.PAYLOAD);
            } else {
                message.setPayload(event.getPayload());
            }
            return message;
        } catch (Exception e) {
            log.error("Exception occured while processing the expression", e);
            throw Errors.EXPRESSION_EVAL_EXCEPTION.getException(e.getMessage());
        }
    }
}
