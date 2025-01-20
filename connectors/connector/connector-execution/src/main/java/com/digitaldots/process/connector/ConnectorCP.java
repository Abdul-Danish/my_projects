/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.process.connector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.digitaldots.api.client.RestClient;
import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.Connectors;
import com.digitaldots.connector.cache.StoreFactory;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.digitaldots.connector.spi.ConnectorResponse;
import com.digitaldots.platform.common.EntityStatus;
import com.digitaldots.platform.scheduler.dto.ConnectorConfig;
import com.digitaldots.platform.solution.entity.Connector;
import com.digitaldots.platform.solution.entity.SecretCategories;
import com.digitaldots.platform.solution.entity.SecretInfo;
import com.digitaldots.process.constant.ProcessConstants;
import com.digitaldots.process.exception.Errors;
import com.digitaldots.secrets.ISecretsManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConnectorCP {

    private static final Map<String, String> CONNECTOR_TYPES = new ConcurrentHashMap<>();
    private static final Map<String, Object> DATA_STORES = new ConcurrentHashMap<>();
    private static final String PAYLOAD = "payload";
    private static final int TWO = 2;
    private static final int HUNDRED = 100;

    public static final String PARAM_NAME_RESPONSE = "response";
    public static final String PARAM_NAME_STATUS_CODE = "statusCode";
    private static final String SERVICE_RESPONSE = "response";

    @Value("${connectorLatestRef}")
    private String dsUrl;

    @Value("${connectorVersionRef}")
    private String dsVersionUrl;

    @Value("${function.gateway.url}")
    private String gatewayConnectionUrl;

    @Value("${secretLatestRef}")
    private String secretLatestRef;

    @Autowired
    private Connectors connectors;

    @Autowired
    private StoreFactory dsFactory;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ISecretsManager secretManager;

    @Autowired
    private PropertiesResolver propResolver;

    @Value("${load.connector.from.classpath:false}")
    private boolean loadCPConnector;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Environment env;

    @PostConstruct
    private void loadSystemMailConnector() throws IOException {
        log.trace("load connectors from classpath is {} ", loadCPConnector);
        if (loadCPConnector) {
            String ConnectorJson = readFromInputStream(this.getClass().getResourceAsStream("/templates/connector.json"));
            ConnectorConfig cc = mapper.readValue(ConnectorJson, ConnectorConfig.class);
            String key = getKey(":", cc.getSolutionId(), cc.getAlternateId(), cc.getVersion());
            Map<String, Object> map = cc.getProperties();
            for (String keyMap : map.keySet()) {
                map.put(keyMap, env.resolvePlaceholders((String) map.get(keyMap.toString())));
            }
            DATA_STORES.put(key, dsFactory.getDBDataSource(cc.getId()).getDataSource(cc.getProperties()));
            log.trace("datasource for {} is loaded ", cc.getId());
        }
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    private Object getResponse(ConnectorResponse response) {
        try {
            if (response.getResponseParameter(PARAM_NAME_STATUS_CODE) != null
                && Integer.parseInt(response.getResponseParameter(PARAM_NAME_STATUS_CODE).toString()) / HUNDRED == TWO) {
                return getRespPayload(response.getResponseParameter(SERVICE_RESPONSE));
            } else {

                if (response.getResponseParameter(SERVICE_RESPONSE) != null) {
                    throw Errors.INVALID_CONNECTOR_RESPONSE.getException(response.getResponseParameter(SERVICE_RESPONSE).toString());
                } else {
                    throw Errors.INVALID_CONNECTOR_RESPONSE.getException(response.getResponseParameters().toString());
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Exception occured while parsing response from service", e);
            throw Errors.INVALID_CONNECTOR_RESPONSE.getException(e);
        }

    }

    private Object getRespPayload(Object response) throws JsonProcessingException {
        if (response instanceof List) {
            return response;
        } else if (response != null && response.toString().startsWith("[")) {
            return new ObjectMapper().readValue(response.toString(), new TypeReference<List<HashMap<String, Object>>>() {
            });
        } else {
            return new ObjectMapper().readValue(response.toString(), Map.class);
        }
    }

    public void loadDS(String id, String dbType, String solutionId) {
        try {
            if (dsFactory.getDBDataSource(dbType) == null) {
                return;
            }
            final String con;
            if ("Email".equalsIgnoreCase(dbType)) {
                con = "connectors";
            } else {
                con = "datastores";
            }
            DATA_STORES.computeIfAbsent(id, (String k) -> {
                ConnectorConfig configuration = restClient.getService(this.dsUrl, ConnectorConfig.class, solutionId, con, k);
                return dsFactory.getDBDataSource(dbType).getDataSource(configuration.getProperties());
            });
            CONNECTOR_TYPES.put(id, dbType);
        } catch (RuntimeException e) {
            log.error("exception while loading the connector details {} {} {} {}", id, dbType, solutionId, e);
        }
    }

    private String getKey(String seperator, String... keys) {
        return String.join(seperator, keys);
    }

    @Synchronized
    public void loadDS(String alternateId, String version, String category, String type, String solutionId, boolean reload) {
        log.debug(" loading the connector with id {} version {} type {} category {} solution {}", alternateId, version, type, category,
            solutionId);
        String key = getKey(":", solutionId, alternateId, version);
        try {
            if (dsFactory.getDBDataSource(type) == null) {
                log.debug("no configuration found for {}", type);
            }
            if (reload) {
                this.close(DATA_STORES.get(key));
                DATA_STORES.remove(key);
            }
            if (DATA_STORES.containsKey(key)) {
                log.debug("DataSource is already loaded");
                return;
            }
            log.trace("Available DataSource keys are {}", DATA_STORES.keySet());
            ConnectorConfig configuration = getConnectionProps(alternateId, version, category, solutionId);
            if (!"system".equalsIgnoreCase(configuration.getSolutionId())) {
                propResolver.resolveProperties(configuration, solutionId);
            }
            log.debug("loading a datasource with id {}", key);
            if (Objects.nonNull(configuration.getSummary())) {
                configuration.getProperties().put(ProcessConstants.FORM_SUMMARY, configuration.getSummary());
            }
            DATA_STORES.put(key, dsFactory.getDBDataSource(type).getDataSource(configuration.getProperties()));
            if (isLatest(configuration)) {
                DATA_STORES.put(getKey(":", solutionId, alternateId, ""), DATA_STORES.get(key));
            }
        } catch (RuntimeException e) {
            log.error("exception while loading the connector details {} {} {} {} {}", alternateId, version, category, solutionId, e);
            throw e;
        }
    }

    private String getVersion(String version) {
        return StringUtils.hasLength(version) ? version : "";
    }

    @Synchronized
    public Object loadDS(Connector connector) {
        log.debug(" loading the connector with id {} version {} type {} in solution {} {} ", connector.getAlternateId(),
            connector.getVersion(), connector.getTemplate(), connector.getCategory(), connector.getSolutionId());
        Object dataSource = null;
        try {
            if (dsFactory.getDBDataSource(connector.getTemplate()) == null) {
                throw new IllegalArgumentException("no configuration found for " + connector.getTemplate());
            }
            getSecrets(connector.getSolutionId(), connector.getProperties());
            Map<String, Object> propeties = connector.getProperties();
            propeties.put(ProcessConstants.ALTERNATE_ID, connector.getAlternateId());
            propeties.put(ProcessConstants.SOLUTION_ID, connector.getSolutionId());
            propeties.put(ProcessConstants.VERSION, connector.getVersion());
            propeties.put(PAYLOAD, connector.getProperties().get(PAYLOAD));
            dataSource = dsFactory.getDBDataSource(connector.getTemplate()).getDataSource(propeties);
            return dsFactory.getDBDataSource(connector.getTemplate()).validate(dataSource, propeties);
        } catch (Exception e) {
            log.error("exception while loading the connector details {} {} {} {} {}", connector.getAlternateId(), connector.getVersion(),
                connector.getTemplate(), connector.getCategory(), connector.getSolutionId(), e);
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            this.close(dataSource);
        }
    }

    private com.digitaldots.connector.spi.Connector getConnector(String type) {
        return connectors.getConnectorById(type);
    }

    private void getSecrets(String solutionId, Map<String, Object> configuration) {
        for (SecretCategories secretCatergory : SecretCategories.values()) {
            if (configuration.values().contains(secretCatergory.getKey()) && configuration.get(secretCatergory.getKey()) != null) {
                configuration
                    .putAll(getCredentailsFromSecret(solutionId, (String) configuration.get(secretCatergory.getKey()), secretCatergory));
            }
        }
    }

    private boolean isLatest(ConnectorConfig configuration) {
        if (configuration.getVersionHistory() == null || configuration.getVersionHistory().isEmpty()) {
            return true;
        }
        List<String> versions = configuration.getVersionHistory();
        log.debug("alternate id {} , versions are {} current version is {}", configuration.getAlternateId(),
            configuration.getVersionHistory(), configuration.getVersion());
        OptionalDouble maxVersion = versions.stream().mapToDouble(Double::parseDouble).max();
        if (maxVersion.isPresent()) {
            return configuration.getVersion().equals("" + maxVersion.getAsDouble());
        } else {
            return true;
        }
    }

    private ConnectorConfig getConnectionProps(String alternateId, String version, String category, String solutionId) {
        Map<String, Object> configuration = null;
        log.debug("loading data {} ,{}", alternateId, version);

        ConnectorConfig connectorConfig = null;
        if (!StringUtils.hasLength(version)) {
            connectorConfig = restClient.getService(this.dsUrl, ConnectorConfig.class, solutionId,
                category.toLowerCase(Locale.getDefault()), alternateId);
        } else {
            connectorConfig = restClient.getService(this.dsVersionUrl, ConnectorConfig.class, solutionId,
                category.toLowerCase(Locale.getDefault()), alternateId, version);
        }

        if (Objects.isNull(connectorConfig)) {
            throw Errors.INVALID_CONNECTOR_CONFG
                .getException(String.format("Invalid connector config found for %s %s %s", category, alternateId, version));
        } else if (!Arrays.asList(EntityStatus.PENDING, EntityStatus.ACTIVE).contains(connectorConfig.getStatus())) {
            // Pending Status will appear here at the time of Publishing
            throw Errors.INVALID_CONNECTOR_CONFG
                .getException(String.format("Invalid connector config found for %s %s %s %s", connectorConfig.getCategory(),
                    connectorConfig.getAlternateId(), connectorConfig.getVersion(), connectorConfig.getStatus()));
        }

        if ("DECISIONS".equalsIgnoreCase(category)) {
            configuration = new HashMap<>();
            configuration.put("id", alternateId);
            configuration.put(ProcessConstants.VERSION, connectorConfig.getVersion());
            configuration.put(ProcessConstants.SOLUTION_ID, solutionId);
            connectorConfig.setProperties(configuration);
            return connectorConfig;
        }
        configuration = connectorConfig.getProperties();
        configuration.putIfAbsent("template", connectorConfig.getTemplate());
        configuration.putIfAbsent(ProcessConstants.VERSION, connectorConfig.getVersion());
        configuration.putIfAbsent("name", connectorConfig.getName());
        configuration.putIfAbsent(ProcessConstants.ALTERNATE_ID, connectorConfig.getAlternateId());
        configuration.putIfAbsent(ProcessConstants.ID, connectorConfig.getId());
        configuration.putIfAbsent(ProcessConstants.SOLUTION_ID, solutionId);
        if (Objects.nonNull(connectorConfig.getAlias())) {
            configuration.putIfAbsent("alias", connectorConfig.getAlias());
        }
        if ("forms".equalsIgnoreCase(category)) {
            configuration.putIfAbsent("id", alternateId);
            configuration.put(ProcessConstants.VERSION, connectorConfig.getVersion());
        } else if ("functions".equalsIgnoreCase(category)) {
            configuration.putIfAbsent("basePath", gatewayConnectionUrl);
        }
        for (SecretCategories secretCatergory : SecretCategories.values()) {
            if (configuration.values().contains(secretCatergory.getKey()) && configuration.get(secretCatergory.getKey()) != null) {
                configuration
                    .putAll(getCredentailsFromSecret(solutionId, (String) configuration.get(secretCatergory.getKey()), secretCatergory));
            }
        }
        return connectorConfig;
    }

    private Map<String, Object> getCredentailsFromSecret(String solutionId, String alternateId, SecretCategories category) {
        SecretInfo secretInfo = restClient.getService(secretLatestRef, SecretInfo.class, solutionId, alternateId);
        if (SecretCategories.CERTIFICATES == category) {
            Map<String, Object> certificateRecord = secretManager.find(solutionId, alternateId, secretInfo.getVaultVersion(), category);
            String cer = (String) certificateRecord.get("cer");
            if (Objects.nonNull(cer)) {
                certificateRecord.put("cer", new String(Base64.getDecoder().decode(cer.split(",")[1])));
                certificateRecord.put("sslContext", createSSLContext(new String(Base64.getDecoder().decode(cer.split(",")[1]))));
            }
            String crl = (String) certificateRecord.get("crl");
            if (Objects.nonNull(crl)) {
                certificateRecord.put("crl", new String(Base64.getDecoder().decode(crl.split(",")[1])));
            }
            return certificateRecord;
        }
        return secretManager.find(solutionId, alternateId, secretInfo.getVaultVersion(), category);
    }

    private static SSLContext createSSLContext(String certficateContent) {
        try (InputStream in = new ByteArrayInputStream(certficateContent.getBytes(StandardCharsets.UTF_8))) {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) cf.generateCertificate(in);
            keystore.setCertificateEntry("caCert", caCert);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keystore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            return sslContext;
        } catch (Exception e) {
            log.error("Unable to create ssl context", e);
            throw new SecurityException(e);
        }
    }

    public Object getConnectionObject(String id) {
        try {
            log.debug("retieving connection object with id {}", id);
            if (DATA_STORES.get(id) instanceof DataSource) {
                return ((DataSource) DATA_STORES.get(id)).getConnection();
            } else {
                return DATA_STORES.get(id);
            }
        } catch (SQLException e) {
            throw new ConnectorException("unable to cast the java class ", e);
        }
    }

    public <T> T getConnectionObject(String id, Class<T> responseClass) {
        if (DATA_STORES.get(id) == null) {
            return null;
        }
        return responseClass.cast(DATA_STORES.get(id));
    }

    @PreDestroy
    void destroy() {
        for (Map.Entry<String, Object> ds : DATA_STORES.entrySet()) {
            this.close(ds.getValue());
        }
    }

    private void close(Object ds) {

        if (ds instanceof Closeable) {
            try {
                ((Closeable) ds).close();
            } catch (IOException e) {
                log.error("unable to close the data source", e);
            }
        }
    }

    public void clearDataSources(String solutionId, String alternateId, String version) {
        DATA_STORES.remove(getKey(":", solutionId, alternateId, version));
        DATA_STORES.remove(getKey(":", solutionId, alternateId, ""));
    }

    public Object executeConnector(Connector connectorRequest) {
        log.debug("Executing the connector with id {} version {} in solution {} ", connectorRequest.getAlternateId(),
            connectorRequest.getVersion(), connectorRequest.getSolutionId());
        try {
            // get connectorCOnfig and use Template to load DS
            ConnectorConfig connectionProps = getConnectionProps(connectorRequest.getAlternateId(), connectorRequest.getVersion(),
                connectorRequest.getCategory().name(), connectorRequest.getSolutionId());
            loadDS(connectorRequest.getAlternateId(), connectorRequest.getVersion(), connectorRequest.getCategory().name(),
                connectionProps.getTemplate(), connectorRequest.getSolutionId(), true);
            Map<String, Object> connectorProps = new HashMap<>();
            connectorProps.putAll(connectorRequest.getPayload());
            connectorProps.put(ProcessConstants.CONNECTOR_ID, connectorRequest.getAlternateId() + ":" + connectorRequest.getVersion());
            Map<String, String> context = new HashMap<>();
            context.put(ProcessConstants.SOLUTION_ID, connectorRequest.getSolutionId());
            connectorProps.put(ProcessConstants.CONTEXT, context);
            com.digitaldots.connector.spi.Connector<ConnectorRequest<? extends ConnectorResponse>> connector = getConnector(
                connectionProps.getTemplate());
            ConnectorRequest<?> request = connector.createRequest();
            request.setRequestParameters(connectorProps);
            ConnectorResponse response = request.execute();
            return getResponse(response);
        } catch (Exception e) {
            log.error("exception occured while loading the connector details {} {} {} {} {}", connectorRequest.getAlternateId(),
                connectorRequest.getVersion(), connectorRequest.getTemplate(), connectorRequest.getCategory(),
                connectorRequest.getSolutionId(), e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
