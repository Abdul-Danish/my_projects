/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.processor;

import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.spi.ConnectorRequest;

import lombok.Getter;

@Aspect
@Getter
public class ConfigureRequestParams {

    @Autowired
    private ConfigurableListableBeanFactory configurableBeanFactory;

    @Before("@annotation(com.digitaldots.connector.annotation.Execute)")
    public void postProcessBeforeInitialization(JoinPoint joinPoint) throws BeansException {
        Object bean = joinPoint.getThis();
        if (Objects.isNull(bean)) {
            return;
        }
        Class<?> clazz = bean.getClass();
        if (!clazz.isAnnotationPresent(Execute.class)) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ConnectorRequest<?>) {
                configureFieldInjection(bean, (ConnectorRequest<?>) args[i]);
                break;
            }
        }

    }

    private void configureFieldInjection(Object bean, ConnectorRequest<?> request) {
        Class<?> managedBeanClass = bean.getClass();
        FieldCallback callback = configurableBeanFactory.getBean(RequestFieldCallBack.class, bean, request);
        ReflectionUtils.doWithFields(managedBeanClass, callback);
    }
}
