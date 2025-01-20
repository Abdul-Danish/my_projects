package com.digitaldots.connector.core;

public abstract class AbstractConnector<Q extends ConnectorRequest<R>, R extends ConnectorResponse> implements Connector<Q> {

    Q target;
    
    String connectorId ;
    
    public AbstractConnector(String connectorId) {
        this.connectorId = connectorId;
    }
    
    public String getId() {
        return connectorId;
    }
    
}
