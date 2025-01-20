/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.annotation.Configure;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.gen.GenericResponse;
import com.digitaldots.connector.gen.impl.GenericResponseImpl;
import com.digitaldots.connector.impl.AbstractConnector;
import com.digitaldots.connector.impl.AbstractConnectorRequest;
import com.digitaldots.connector.impl.AbstractRequestInvocation;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import lombok.Getter;

@SupportedAnnotationTypes({ "com.digitaldots.connector.annotation.Connector" })
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ConnectorProcessor extends AbstractProcessor {

    private static final String CONNECTOR = "Connector";
    private static final String SERVICE_IMPL = "ServiceImpl";
    private static final String REQUEST_IMPL = "RequestImpl";
    private static final String REQUEST = "Request";
    private static final String INVOCATION_IMPL = "InvocationImpl";
    private static final String CONNECTOR_IMPL = "ConnectorImpl";
    private static AnnotationSpec getter = AnnotationSpec.builder(Getter.class).build();
    private static AnnotationSpec component = AnnotationSpec.builder(Component.class).build();
    private static AnnotationSpec autowire = AnnotationSpec.builder(Autowired.class).build();
    private static AnnotationSpec setParams = AnnotationSpec.builder(Params.class).build();
    private static AnnotationSpec scope = AnnotationSpec.builder(Scope.class).addMember("value", "\"prototype\"").build();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Connector.class);
        for (Element element : elements) {
            if (element.getKind() != ElementKind.CLASS) {
                throw new IllegalArgumentException("Connector annotation can be added to class only");
            }
            Connector connectorAnnotation = element.getAnnotation(Connector.class);
            String pkg = getPackageName(element);
            ClassName serviceClass;
            try {
                serviceClass = createServiceClass(pkg, element, connectorAnnotation, element);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }

            Execute[] excuteMethods = element.getAnnotationsByType(Execute.class);
            if (excuteMethods == null || excuteMethods.length > 1) {
                throw new IllegalArgumentException("Connector Have multuple @Execute annotations in the class");
            }
            try {
                ClassName reqInterfaceType = createRequestClass(pkg, connectorAnnotation);
                createRequestImpl(pkg, element, connectorAnnotation, reqInterfaceType);
                ClassName connctorInterface = createConncetorClass(pkg, connectorAnnotation, reqInterfaceType);
                ClassName reqInvocation = createRequestInvocationClass(pkg, element, connectorAnnotation, reqInterfaceType, serviceClass);
                createConncetorClassImpl(pkg, connectorAnnotation, reqInterfaceType, connctorInterface, reqInvocation, serviceClass);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }

        }
        return true;
    }

    private ClassName createConncetorClassImpl(String pkg, Connector connectorAnnotation, ClassName reqInterfaceType,
        ClassName connctorInterface, ClassName reqInvocation, ClassName serviceClass) throws IOException {
        TypeName.get(com.digitaldots.connector.spi.Connector.class);
        FieldSpec field = FieldSpec.builder(reqInvocation.box(), "invocation").addAnnotation(autowire).build();
        FieldSpec srvClass = FieldSpec.builder(serviceClass, "service", Modifier.PRIVATE).addAnnotation(autowire).build();
        FieldSpec appCxt = FieldSpec.builder(AutowireCapableBeanFactory.class, "appCxt", Modifier.PRIVATE).addAnnotation(autowire).build();
        MethodSpec constructor = MethodSpec.constructorBuilder().addStatement("super($S);", connectorAnnotation.id()).build();
        CodeBlock executeBlock = CodeBlock.builder().addStatement("$T service = getService()", serviceClass).addStatement(
            "$T json = new $T();\n json.put(\"statusCode\", 202); \n json.put(\"response\", service.execute(request)); \n appCxt.destroyBean(service); return new $T(json)",
            JSONObject.class, JSONObject.class, GenericResponseImpl.class).build();
        MethodSpec execute = MethodSpec.methodBuilder("execute").returns(GenericResponse.class).addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class).returns(GenericResponse.class).addCode(executeBlock).addParameter(reqInterfaceType, "request")
            .build();
        MethodSpec getInvocation = MethodSpec.methodBuilder("getInvocation").returns(reqInvocation)
            .addStatement("return appCxt.getBean($L.class)", reqInvocation.box()).build();
        MethodSpec getService = MethodSpec.methodBuilder("getService").returns(serviceClass)
            .addStatement("return appCxt.getBean($L.class)", serviceClass).build();

        MethodSpec createRequest = MethodSpec.methodBuilder("createRequest").returns(reqInterfaceType).addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class).addStatement("return appCxt.getBean($L.class, this)", reqInterfaceType.box()).build();
        AnnotationSpec configure = AnnotationSpec.builder(Configure.class).addMember("id", "$S", connectorAnnotation.id()).build();
        TypeSpec requestInterface = TypeSpec.classBuilder(connectorAnnotation.id() + CONNECTOR_IMPL).addField(field).addField(appCxt)
            .addField(srvClass).addMethod(constructor).addMethod(execute).addMethod(getInvocation).addMethod(createRequest)
            .addMethod(getService).addAnnotation(component).addAnnotation(configure).addSuperinterface(connctorInterface)
            .superclass(
                ParameterizedTypeName.get(ClassName.get(AbstractConnector.class), reqInterfaceType, ClassName.get(GenericResponse.class)))
            .build();
        String clPkg = pkg + "." + connectorAnnotation.id() + CONNECTOR_IMPL;
        JavaFile file = JavaFile.builder(pkg, requestInterface).indent("  ").build();
        writeClass(clPkg, file);
        return ClassName.get(pkg, connectorAnnotation.id() + CONNECTOR_IMPL);
    }

    private ClassName createConncetorClass(String pkg, Connector connectorAnnotation, ClassName reqInterfaceType) throws IOException {
        TypeName.get(com.digitaldots.connector.spi.Connector.class);
        TypeSpec requestInterface = TypeSpec.interfaceBuilder(connectorAnnotation.id() + CONNECTOR)
            .addSuperinterface(ParameterizedTypeName.get(ClassName.get(com.digitaldots.connector.spi.Connector.class), reqInterfaceType))
            .build();
        String clPkg = pkg + "." + connectorAnnotation.id() + CONNECTOR;
        JavaFile file = JavaFile.builder(pkg, requestInterface).indent("  ").build();
        writeClass(clPkg, file);
        return ClassName.get(pkg, connectorAnnotation.id() + CONNECTOR);
    }

    private ClassName createRequestInvocationClass(String pkg, Element element, Connector connector, ClassName reqInterfaceType,
        ClassName serviceClass) throws IOException {
        FieldSpec field = FieldSpec.builder(serviceClass.box(), "service", Modifier.PRIVATE).addAnnotation(autowire).build();
        MethodSpec invokeTarget = MethodSpec.methodBuilder("invokeTarget").addCode("return service.execute(target);").returns(Object.class)
            .addModifiers(Modifier.PUBLIC).addAnnotation(setParams).build();
        TypeSpec requestImpl = TypeSpec.classBuilder(connector.id() + INVOCATION_IMPL).addJavadoc(element.toString(), "").addField(field)
            .addAnnotation(getter) // .addSuperinterface(reqInterfaceType.box())
            .superclass(ParameterizedTypeName.get(ClassName.get(AbstractRequestInvocation.class), reqInterfaceType))
            .addAnnotation(component).addAnnotation(scope).addMethod(invokeTarget).build();
        String clPkg = pkg + "." + connector.id() + INVOCATION_IMPL;
        JavaFile file = JavaFile.builder(pkg, requestImpl).indent("  ").build();
        writeClass(clPkg, file);
        return ClassName.get(pkg, connector.id() + INVOCATION_IMPL);
    }

    private ClassName createServiceClass(String pkg, Element element, Connector connector, Element serviceClazz) throws IOException {
        TypeName cn = ClassName.get(pkg, serviceClazz.getSimpleName().toString()).box();
        TypeSpec.Builder serviceImpl = TypeSpec.classBuilder(connector.id() + SERVICE_IMPL).addJavadoc(element.toString(), "")
            .superclass(cn).addAnnotation(component).addAnnotation(scope);
        Map<String, TypeName> fields = FieldInfo.getFields(element);
        Iterable<MethodSpec> methodSpecs = FieldInfo.getSettersGetters(fields);
        serviceImpl.addMethods(methodSpecs);
        String clPkg = pkg + "." + connector.id() + SERVICE_IMPL;
        JavaFile file = JavaFile.builder(pkg, serviceImpl.build()).indent("  ").build();
        writeClass(clPkg, file);
        return ClassName.get(pkg, connector.id() + SERVICE_IMPL);
    }

    private ClassName createRequestImpl(String pkg, Element element, Connector connector, ClassName reqInterfaceType) throws IOException {
        TypeName fieldType = TypeName.get(element.asType());
        FieldSpec field = FieldSpec.builder(fieldType, "implClass", Modifier.PRIVATE).addAnnotation(autowire).build();
        CodeBlock codeBlock = CodeBlock.builder().add("System.out.println(\" testing\");").build();
        MethodSpec.methodBuilder("execute").addCode(codeBlock).build();
        MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
            .addParameter(com.digitaldots.connector.spi.Connector.class, "connector").addStatement("super(connector);").build();
        TypeName reqType = reqInterfaceType.box();
        TypeSpec requestImpl = TypeSpec.classBuilder(connector.id() + REQUEST_IMPL).addJavadoc(element.toString(), "").addField(field)
            .addAnnotation(getter).addSuperinterface(reqType).addMethod(constructor)
            .superclass(ParameterizedTypeName.get(ClassName.get(AbstractConnectorRequest.class), TypeName.get(GenericResponse.class)))
            .addAnnotation(component).addAnnotation(scope).build();
        String clPkg = pkg + "." + connector.id() + REQUEST_IMPL;
        JavaFile file = JavaFile.builder(pkg, requestImpl).indent("  ").build();
        writeClass(clPkg, file);
        return ClassName.get(pkg, connector.id() + REQUEST_IMPL);
    }

    private ClassName createRequestClass(String pkg, Connector connector) throws IOException {
        TypeName.get(ConnectorRequest.class);
        TypeSpec requestInterface = TypeSpec.interfaceBuilder(connector.id() + REQUEST)
            .addSuperinterface(ParameterizedTypeName.get(ConnectorRequest.class, GenericResponse.class)).build();
        String clPkg = pkg + "." + connector.id() + REQUEST;
        JavaFile file = JavaFile.builder(pkg, requestInterface).indent("  ").build();
        writeClass(clPkg, file);
        return ClassName.get(pkg, connector.id() + REQUEST);
    }

    private String getPackageName(Element element) {
        List<PackageElement> packageElements = ElementFilter.packagesIn(Arrays.asList(element.getEnclosingElement()));

        Optional<PackageElement> packageElement = packageElements.stream().findFirst();
        if (packageElement.isPresent()) {
            return packageElement.get().getQualifiedName().toString();
        }
        return null;
    }

    private void writeClass(String name, JavaFile file) throws IOException {
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(name);
        Writer writer = sourceFile.openWriter();
        file.writeTo(writer);
        writer.close();
    }
}
