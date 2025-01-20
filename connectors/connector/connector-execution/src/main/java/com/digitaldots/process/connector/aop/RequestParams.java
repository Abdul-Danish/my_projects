package com.digitaldots.process.connector.aop;

import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.process.connector.ConnectorCP;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class RequestParams {

    @Autowired
    ConnectorCP beverCP;

    @Autowired
    private ConfigurableListableBeanFactory configurableBeanFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Before("@annotation(com.digitaldots.connector.annotation.Execute)")
    public void preProcessBeforeInitialization(JoinPoint joinPoint) throws BeansException {
        log.trace("process execute annotation");
        Object bean = joinPoint.getThis();
        log.trace("bean is {}", bean);
        if (Objects.isNull(bean)) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        log.trace("arguments are {}", args);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ConnectorRequest<?>) {
                log.trace("conenctor arg is {}", args[i]);
                configureFieldInjection(bean, (ConnectorRequest<?>) args[i]);
                break;
            }
        }

    }

    private void configureFieldInjection(Object bean, ConnectorRequest<?> request) {
        Class<?> managedBeanClass = bean.getClass();
        log.trace("request for params {}", request);
        FieldCallback callback = configurableBeanFactory.getBean(ParamCallBack.class, bean, request, beverCP, objectMapper);

        ReflectionUtils.doWithFields(managedBeanClass, callback);
    }

    @After("@annotation(com.digitaldots.connector.annotation.Execute)")
    public void postProcessBeforeInitialization(JoinPoint joinPoint) {
        log.trace("processing post @Execute");

        Object[] args = joinPoint.getArgs();
        log.trace("arguments are {}", args);
        Object bean = joinPoint.getThis();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ConnectorRequest<?>) {
                FieldCallback callback = configurableBeanFactory.getBean(AfterExecutionCallBack.class, bean, (ConnectorRequest<?>) args[i],
                    beverCP, objectMapper);
                ReflectionUtils.doWithFields(bean.getClass(), callback);
                break;
            }
        }
    }
}