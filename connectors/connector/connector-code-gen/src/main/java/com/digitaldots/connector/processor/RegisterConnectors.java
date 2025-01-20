/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.Connectors;
import com.digitaldots.connector.annotation.Configure;
import com.digitaldots.connector.spi.Connector;

import lombok.Getter;

/**
 * registers the conenctor classes to the registry - when annotated with @Configure at the class level.
 * 
 * @author msrsr
 *
 */
@Component
@Getter
public class RegisterConnectors implements BeanPostProcessor {

    @Autowired
    private Connectors connectors;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(Configure.class) && (bean instanceof Connector)) {
            connectors.registerConnectorInstance(clazz.getAnnotationsByType(Configure.class)[0].id(), (Connector) bean);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
