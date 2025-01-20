package com.digitaldots.process.connector.aop;

import java.lang.reflect.Field;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.process.connector.ConnectorCP;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
@AllArgsConstructor
public class AfterExecutionCallBack implements FieldCallback {

    private Object bean;
    private ConnectorRequest<?> connectorRequest;

    ConnectorCP beverCP;

    ObjectMapper objectMapper;

    @Override
    public void doWith(Field field) throws IllegalAccessException {
        if (field.isAnnotationPresent(Params.class)) {
            log.trace("remove key {} ", field.getDeclaredAnnotation(Params.class).name());
            connectorRequest.getRequestParameters().remove(field.getDeclaredAnnotation(Params.class).name());
        }
    }

}