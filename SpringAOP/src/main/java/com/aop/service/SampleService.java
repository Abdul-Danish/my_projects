package com.aop.service;

import org.springframework.stereotype.Component;

import com.aop.annotations.ExecutionTime;
import com.aop.annotations.LogHelper;
import com.aop.annotations.ProcessStatus;
import com.aop.model.Sample;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SampleService {

    @ExecutionTime
    @ProcessStatus
    @LogHelper
    public Sample execute(Sample sample) {
        try {
            log.info("obj: {}", sample.toString());
            process1();
            process2();
        } catch (Exception e) {
//            log.error("Custom Exception");
            throw e;
        }
        return sample;
    }
    
    private void process1() {
//        throw new RuntimeException("CUSTOM EXCEPTION");
    }
    
    private void process2() {
        log.info("continuing processing");
    }
}
