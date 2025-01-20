package com.digitaldots.connector.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;

@Connector(id = "FUNCTION")
public abstract class FunctionService {

    @Autowired
    private ApplicationContext applicationContext;

    @Execute
    public Map<String, Object> execute(FUNCTIONRequest request) {

        Map<String, Object> requestParameters = request.getRequestParameters();
        Handler handler = applicationContext.getBean(Handler.class);
        Map<String, Object> response = handler.handle((Map) requestParameters, (Map) requestParameters);

//        Map<String, Object> response = new HashMap<>();
//        response.putAll(request.getRequestParameters());
        return response;
    }

}