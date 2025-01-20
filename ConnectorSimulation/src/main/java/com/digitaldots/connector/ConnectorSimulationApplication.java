package com.digitaldots.connector;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.digitaldots.connector.core.Connector;
import com.digitaldots.connector.core.ConnectorRequest;
import com.digitaldots.connector.core.ConnectorResponse;
import com.digitaldots.connector.core.Connectors;

@SpringBootApplication
public class ConnectorSimulationApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ConnectorSimulationApplication.class, args);
    }

    @Autowired
    Connectors<Connector<? extends ConnectorRequest<? extends ConnectorResponse>>> connectors;

    @Override
    public void run(String... args) throws Exception {
        Connector<? extends ConnectorRequest<? extends ConnectorResponse>> connector = connectors.getConnectorByType("SampleConnector");
        ConnectorRequest<? extends ConnectorResponse> request = connector.createRequest();
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", "Sample");
        request.setRequestParameters(payload);
        ConnectorResponse execute = request.execute();
        System.out.println("RESPONSE: " + execute.getResponse());
        System.out.println("RESPONSE TEXT: " + execute.getResponse().get("response"));
    }

    @Bean
    public JSONObject jsonObjectBean() {
        return new JSONObject();
    }

}
