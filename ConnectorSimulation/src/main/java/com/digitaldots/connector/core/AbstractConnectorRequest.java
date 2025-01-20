package com.digitaldots.connector.core;

import java.util.Map;

public abstract class AbstractConnectorRequest<R extends ConnectorResponse> implements ConnectorRequest<R> {

    public Connector<ConnectorRequest<? extends ConnectorResponse>> connector;
    
    private Map<String, Object> requestParams;

    public AbstractConnectorRequest(Connector<ConnectorRequest<? extends ConnectorResponse>> connector) {
        this.connector = connector;
    }

    @Override
    public R execute() {
        return connector.execute(this);
    }

    @Override
    public void setRequestParameters(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }
    
    @Override
    public Map<String, Object> getRequestParameters() {
        return requestParams;
    }
    
}
