package com.digitaldots.connector.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.core.AbstractConnectorInvocation;

@Component
public class SampleConnectorInvocationImpl extends AbstractConnectorInvocation<SampleConnectorRequest> {

    @Autowired
    private SampleConnectorServiceImpl implService;
    
    public Object invokeTarget() {
        return implService.execute(target);
    }

   
    
}
