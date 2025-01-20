/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.processor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RequestFieldCallBack implements FieldCallback {

    private Object bean;
    private ConnectorRequest<?> connectorRequest;

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        if (!field.isAnnotationPresent(Params.class)) {
            return;
        }
        ReflectionUtils.makeAccessible(field);
        String key = field.getDeclaredAnnotation(Params.class).name();
        try {
            new PropertyDescriptor(key, bean.getClass()).getWriteMethod().invoke(key, connectorRequest.getRequestParameter(key));
        } catch (InvocationTargetException | IntrospectionException e) {
            throw new ConnectorException("unable to set the value for " + field.getName(), e);
        }
    }

}
