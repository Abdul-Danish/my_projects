package com.aop.aspect;

import java.lang.ProcessHandle.Info;
import java.lang.reflect.Method;

import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.aop.annotations.LogHelper;
import com.aop.annotations.ProcessStatus;
import com.aop.model.Sample;
import com.aop.service.SampleService;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class SampleAspect {
    
    /*
     * Default Order is specified based on the order aspects were discovered by Spring
     */
    
    @AfterThrowing("@annotation(com.aop.annotations.ExecutionTime)")
    public void onError(JoinPoint joinPoint) {
        // control comes to AfterThrows first
        log.info("inside OnError");
    }
    
    @Around("@annotation(com.aop.annotations.ExecutionTime)")
    public void executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
        log.info("inside log execution time");
        Object target = joinPoint.getTarget();
        if (target instanceof SampleService) {
            Sample obj = ((SampleService) target).execute(Sample.builder().id("200").title("EA").build());
            log.info("target: {}", obj.toString());
        }
        
        log.info("Kind: {}", joinPoint.getKind());
        log.info("Args: {}", joinPoint.getArgs());
        log.info("Source Location: {}", joinPoint.getSourceLocation());
        log.info("Sig: {}", joinPoint.getSignature());
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Method Name: {}", signature.getName());
        log.info("paremeter type {}, name {}", signature.getParameterTypes(), signature.getParameterNames()); 
        Method method = signature.getMethod();
        ProcessStatus annotation = method.getAnnotation(ProcessStatus.class);
        log.info("annotation: {}", annotation.status());
        
        long start = System.currentTimeMillis();
        joinPoint.proceed();
        log.info("Method completed exeution in {} ms", System.currentTimeMillis() - start);
        log.info("exited execution time");
        } catch (Exception e) {
            // control comes to catch block second
            log.error("inside catch of ExecutionTime");
        }
    }
    
    @Around("@annotation(com.aop.annotations.LogHelper)")
    public void logHelper(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("inside log helper");
        joinPoint.proceed();
        log.info("exited log helper");
    }

    @Around("@annotation(com.aop.annotations.ProcessStatus)")
    public void processStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("inside process status");
        joinPoint.proceed();
        log.info("exited process status");
    }
    
}
