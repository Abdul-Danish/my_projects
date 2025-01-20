package com.digitaldots.connector.core;

public abstract class AbstractConnectorInvocation<Q extends ConnectorRequest<? extends ConnectorResponse>>  implements ConnectorInvocation<Q> {

    protected Q target;
    
    public abstract Object invokeTarget();
}
