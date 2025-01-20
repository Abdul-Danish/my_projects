package com.digitaldots.connector.core;

public interface Connector<Q extends ConnectorRequest<? extends ConnectorResponse>> {

    <T extends ConnectorResponse> T execute(Q connectorRequest);

    Q createRequest();
    
    String getId();

}
