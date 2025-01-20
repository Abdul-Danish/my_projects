/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.annotation.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.ElementFilter;

import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Params;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

public class FieldInfo {

    private static AnnotationSpec params = AnnotationSpec.builder(Params.class).build();
    private static AnnotationSpec connection = AnnotationSpec.builder(Connection.class).build();

    private final Map<String, Class<?>> fields;

    public FieldInfo(Map<String, Class<?>> fields) {

        this.fields = fields;
    }

    public Map<String, Class<?>> getFields() {
        return fields;
    }

    public static Map<String, TypeName> getFields(Element element) {
        Map<String, TypeName> fields = new HashMap<>();

        for (Element executableElement : ElementFilter.fieldsIn(element.getEnclosedElements())) {
            if (executableElement.getKind() == ElementKind.FIELD && ((executableElement.getAnnotationsByType(Params.class).length > 0)
                || (executableElement.getAnnotationsByType(Connection.class).length > 0))) {
                String fieldName = executableElement.getSimpleName().toString();
                fields.put(fieldName, TypeName.get(executableElement.asType()).box());
            }
        }
        return fields;
    }

    public static Iterable<MethodSpec> getSettersGetters(Map<String, TypeName> fields) {
        List<MethodSpec> methods = new ArrayList<>();
        for (Map.Entry<String, TypeName> field : fields.entrySet()) {
            if (field.getValue().annotated(params) != null || field.getValue().annotated(connection) != null
                || !"LOG".equalsIgnoreCase(field.getKey())) {
                String method = field.getKey().substring(0, 1).toUpperCase(Locale.getDefault()) + field.getKey().substring(1);
                methods.add(MethodSpec.methodBuilder("set" + method).addParameter(field.getValue(), field.getKey())
                    .addCode("this." + field.getKey() + "=" + field.getKey() + " ;").addModifiers(Modifier.PUBLIC).build());
                methods.add(MethodSpec.methodBuilder("get" + method).returns(field.getValue())
                    .addCode("return this." + field.getKey() + ";").addModifiers(Modifier.PUBLIC).build());
            }
        }
        return methods;
    }
}
