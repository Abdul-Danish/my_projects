package com.digitaldots.connector.handler;

import org.springframework.stereotype.Component;

import com.digitaldots.connector.core.AbstractConnectorRequest;
import com.digitaldots.connector.core.Connector;
import com.digitaldots.connector.core.GenericResponse;

@Component
public class SampleConnectorRequestImpl extends AbstractConnectorRequest<GenericResponse> implements SampleConnectorRequest {

    public SampleConnectorRequestImpl(Connector connector) {
        super(connector);
    }
}
