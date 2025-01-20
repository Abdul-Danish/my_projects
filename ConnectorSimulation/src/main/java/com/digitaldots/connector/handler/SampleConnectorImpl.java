package com.digitaldots.connector.handler;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.core.AbstractConnector;
import com.digitaldots.connector.core.GenericResponse;
import com.digitaldots.connector.core.GenericResponseImpl;

@Component
public class SampleConnectorImpl extends AbstractConnector<SampleConnectorRequest, GenericResponse> implements SampleConnector {

    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private SampleConnectorServiceImpl serviceImpl;
    
    @Autowired
    private SampleConnectorInvocationImpl invocationImpl;
    
    public SampleConnectorImpl() {
        super("SampleConnector");
    }
 
    @Override
    public GenericResponse execute(SampleConnectorRequest connectorRequest) {
        SampleConnectorServiceImpl service = getService();
        Map<String, Object> execute = service.execute(connectorRequest);
        System.out.println("Execution Response is: " + execute.get("response"));
        Map<String, Object> response = new HashMap<>();
        response.put("response", execute.get("response"));
        return new GenericResponseImpl(new JSONObject(response));
    }

    @Override
    public SampleConnectorRequest createRequest() {
        return context.getBean(SampleConnectorRequest.class);
    }
    
    public SampleConnectorServiceImpl getService() {
        return context.getBean(SampleConnectorServiceImpl.class);
    }

}
