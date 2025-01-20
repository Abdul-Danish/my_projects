package com.digitaldots.process.connector;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.digitaldots.api.client.RestClient;
import com.digitaldots.connector.ConnectorException;
import com.digitaldots.platform.foundation.entity.Solution;
import com.digitaldots.platform.scheduler.dto.ConnectorConfig;
import com.digitaldots.process.exception.Errors;
import com.digitaldots.process.variables.Variables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PropertiesResolver {

    @Value("${FOUNDATION_API_HOST:http://localhost:8040}/api/${foundation.api.version}/solutions/{0}")
    private String solutionConfigURL;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Variables variables;

    public Map<String, Object> resolveProperties(Map<String, Object> properties, String solutionId) {
        Solution solution = getSolution(solutionId);
        log.debug("resolving properties for solution {}", solution.getName());
        if (Objects.isNull(solution)) {
            throw Errors.ID_NOT_FOUND_ERROR.getException();
        }
        if (Objects.isNull(properties) || properties.isEmpty()) {
            return properties;
        }
        Map<String, Object> solutionConfig = solution.getConfig();
        Map<String, Object> updatedProperties = extracted(properties, solutionConfig);
        return updatedProperties;
    }

    public ConnectorConfig resolveProperties(ConnectorConfig connectorConfig, String solutionId) {
        Map<String, Object> updatedProperties = resolveProperties(connectorConfig.getProperties(), solutionId);
        connectorConfig.setProperties(updatedProperties);
        return connectorConfig;
    }

    private Map<String, Object> extracted(Map<String, Object> properties, Map<String, Object> solutionConfig) {
        try {
            String propertiesString = objectMapper.writeValueAsString(properties);
            TypeReference<Map<String, Object>> parameterizedTypeReference = new TypeReference<Map<String, Object>>() {
            };
            return objectMapper.readValue(variables.getForObject(propertiesString, String.class, solutionConfig),
                parameterizedTypeReference);
        } catch (JsonProcessingException ex) {
            log.error("Exception occured while evaluating the expression for properties {}", properties, ex);
            throw new ConnectorException(ex.getMessage());
        }

    }

    private Solution getSolution(String solutionId) {
        return restClient.getService(solutionConfigURL, Solution.class, solutionId);
    }

}
