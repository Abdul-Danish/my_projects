package com.digitaldots.process.connector.aop;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.process.connector.ConnectorCP;
import com.digitaldots.process.constant.ProcessConstants;
import com.digitaldots.process.exception.Errors;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
@AllArgsConstructor
public class ParamCallBack implements FieldCallback {

    private Object bean;
    private ConnectorRequest<?> connectorRequest;

    ConnectorCP beverCP;

    ObjectMapper objectMapper;

    @Override
    public void doWith(Field field) throws IllegalAccessException {
        if (field.isAnnotationPresent(Params.class)) {
            updateParam(field);
        } else if (field.isAnnotationPresent(Connection.class)) {
            updateConnection(field);
        }
    }

    private void updateConnection(Field field) throws IllegalAccessException {
        String id = (String) ((Map) connectorRequest.getRequestParameter(ProcessConstants.CONTEXT)).get(ProcessConstants.SOLUTION_ID) + ":"
            + connectorRequest.getRequestParameter("custom:connectorId");
        log.trace("connector key is {} ", id);
        try {
            Optional<PropertyDescriptor> propertyDescriptor = getPd(field);
            log.trace("found {} ", propertyDescriptor.isPresent());

            if (propertyDescriptor.isPresent() && beverCP.getConnectionObject(id) == null) {
                throw Errors.INVALID_CONNECTOR_CONFG.getException();
            } else if (propertyDescriptor.isPresent()) {
                propertyDescriptor.get().getWriteMethod().invoke(bean, field.getType().cast(beverCP.getConnectionObject(id)));
            }
            field.setAccessible(false);
        } catch (SecurityException | InvocationTargetException | IntrospectionException e) {
            throw new ConnectorException("unable to set the value for " + field.getName(), e);
        }

    }

    private Optional<PropertyDescriptor> getPd(Field field) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        return Arrays.stream(beanInfo.getPropertyDescriptors()).filter(pd -> pd.getName().equals(field.getName())).findAny();

    }

    private void updateParam(Field field) throws IllegalAccessException {
        log.trace("field is {}", field);
        ReflectionUtils.makeAccessible(field);
        field.setAccessible(true);
        String key = field.getDeclaredAnnotation(Params.class).name();
        if (connectorRequest.getRequestParameter(key) == null) {
            log.trace(" the value for {} is null", key);
            return;
        }
        log.trace("key is {} value is {}", key, connectorRequest.getRequestParameter(key));
        try {
            Optional<PropertyDescriptor> propertyDescriptor = getPd(field);
            log.trace("found {} ", propertyDescriptor.isPresent());
            if (propertyDescriptor.isPresent()) {
                log.trace("processing field :: {} ", field.getType().getName());
                if ("org.json.JSONObject".equals(field.getType().getName())) {
                    propertyDescriptor.get().getWriteMethod().invoke(bean,
                        objectMapper.convertValue(connectorRequest.getRequestParameter(key), JSONObject.class));
                } else if ("java.util.Map".equals(field.getType().getName())) {
                    ParameterizedType type = (ParameterizedType) field.getGenericType();
                    JavaType jt = objectMapper.getTypeFactory().constructMapType(Map.class,
                        Class.forName(type.getActualTypeArguments()[0].getTypeName()),
                        Class.forName(type.getActualTypeArguments()[1].getTypeName()));
                    propertyDescriptor.get().getWriteMethod().invoke(bean, getType(connectorRequest.getRequestParameter(key), jt));
                } else if ("java.util.List".equals(field.getType().getName())) {
                    ParameterizedType type = (ParameterizedType) field.getGenericType();
                    JavaType jt = objectMapper.getTypeFactory().constructCollectionLikeType(List.class,
                        Class.forName(type.getActualTypeArguments()[0].getTypeName()));
                    propertyDescriptor.get().getWriteMethod().invoke(bean, getType(connectorRequest.getRequestParameter(key), jt));
                } else if ("java.lang.String".equals(field.getType().getName()) || field.getType().isPrimitive()) {
                    propertyDescriptor.get().getWriteMethod().invoke(bean, connectorRequest.getRequestParameter(key).toString());
                } else {
                    JavaType jt = objectMapper.getTypeFactory().constructType(field.getType());
                    propertyDescriptor.get().getWriteMethod().invoke(bean, getType(connectorRequest.getRequestParameter(key), jt));
                }
            }
            field.setAccessible(false);
        } catch (SecurityException | InvocationTargetException | IntrospectionException | ClassNotFoundException
            | IllegalArgumentException e) {
            log.error("Exception occured while updating params", e);
            throw new ConnectorException("unable to set the value for " + field.getName(), e);
        }
    }

    private Object getType(Object requestParameter, JavaType jt) {
        return objectMapper.convertValue(requestParameter, jt);
    }

}