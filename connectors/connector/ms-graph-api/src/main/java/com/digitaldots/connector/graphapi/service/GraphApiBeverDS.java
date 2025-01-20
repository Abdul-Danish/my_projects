/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.graphapi.service;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.azure.identity.AuthorizationCodeCredential;
import com.azure.identity.AuthorizationCodeCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.cache.Store;
import com.digitaldots.connector.graphapi.util.GraphApiConstants;
import com.digitaldots.connector.graphapi.util.GraphApiHelper;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GraphApiBeverDS implements Store<GraphApiHelper, GraphApiHelper> {

    @Override
    public String getDBTye() {
        return "MicrosoftGraph";
    }

    @Override
    public GraphApiHelper getConnection(GraphApiHelper graphApiHelper) {
        return graphApiHelper;
    }

    @Override
    public GraphApiHelper getDataSource(Map<String, Object> configProperties) {
        final String AUTH_METHOD = "authMethod";
        GraphApiHelper helper = null;
        if (Objects.equals(GraphApiConstants.CLIENT_CREDENTIALS_PROVIDER, configProperties.get(AUTH_METHOD))) {
            helper = clientCredentialsProvider(configProperties);
        } else if (Objects.equals(GraphApiConstants.AUTHORIZATION_CODE_PROVIDER, configProperties.get(AUTH_METHOD))) {
            helper = authorizationCodeProvider(configProperties);
        } else if (Objects.equals(GraphApiConstants.DEVICE_CODE_PROVIDER, configProperties.get(AUTH_METHOD))) {
            helper = deviceCodeProvider(configProperties);
        } else if (Objects.equals(GraphApiConstants.USER_PASSWORD_CREDENTIALS, configProperties.get(AUTH_METHOD))) {
            helper = userPasswordCredentials(configProperties);
        } else if (Objects.equals(GraphApiConstants.INTERACTIVE_PROVIDER, configProperties.get(AUTH_METHOD))) {
            helper = interactiveProvider(configProperties);
        } else if (Objects.equals(GraphApiConstants.NO_AUTH, configProperties.get(AUTH_METHOD))
            && Objects.nonNull(configProperties.get("webhookUrl"))) {
            helper = GraphApiHelper.builder().webhookUrl(configProperties.get("webhookUrl").toString()).build();
        } else {
            throw new IllegalArgumentException("Invalid Authentication Type selected");
        }
        return helper;
    }

    private static GraphApiHelper clientCredentialsProvider(Map<String, Object> configProperties) {
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.CCP_CLIENT_ID))) {
            throw new ConnectorException(GraphApiConstants.INVALID_CLIENT_ID);
        }
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.CCP_CLIENT_SECRET))) {
            throw new ConnectorException(GraphApiConstants.INVALID_CLIENT_SECRET);
        }
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.CCP_TENANT_ID))) {
            throw new ConnectorException(GraphApiConstants.INVALID_TENENT_ID);
        }
        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
            .clientId(configProperties.get(GraphApiConstants.CCP_CLIENT_ID).toString())
            .clientSecret(configProperties.get(GraphApiConstants.CCP_CLIENT_SECRET).toString())
            .tenantId(configProperties.get(GraphApiConstants.CCP_TENANT_ID).toString()).build();

        // final TokenCredentialAuthProvider tokenCredentialAuthProvider = new AzureIdentityAuthenticationProvider(clientSecretCredential);
        // GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient();

        GraphServiceClient graphServiceClient = new GraphServiceClient(clientSecretCredential);
        return GraphApiHelper.builder().graphClient(graphServiceClient).build();
    }

    private static GraphApiHelper authorizationCodeProvider(Map<String, Object> configProperties) {
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.ACP_CLIENT_ID))) {
            throw new ConnectorException(GraphApiConstants.INVALID_CLIENT_ID);
        }
        if (!StringUtils.hasLength((String) configProperties.get("acpClientSecret"))) {
            throw new ConnectorException(GraphApiConstants.INVALID_CLIENT_SECRET);
        }
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.ACP_AUTHORIZATION_CODE))) {
            throw new ConnectorException("Authorization Code not Provided");
        }
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.ACP_REDIRECT_URL))) {
            throw new ConnectorException("Redirect Url not Provided");
        }
        final AuthorizationCodeCredential authCodeCredential = new AuthorizationCodeCredentialBuilder()
            .clientId(configProperties.get(GraphApiConstants.ACP_CLIENT_ID).toString())
            .clientSecret(configProperties.get(GraphApiConstants.ACP_CLIENT_SECRET).toString())
            .authorizationCode(configProperties.get(GraphApiConstants.ACP_AUTHORIZATION_CODE).toString())
            .redirectUrl(configProperties.get("acpRedirectUrl").toString()).build();

        // final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(authCodeCredential);
        // GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient()

        GraphServiceClient graphServiceClient = new GraphServiceClient(authCodeCredential);
        return GraphApiHelper.builder().graphClient(graphServiceClient).build();
    }

    private static GraphApiHelper deviceCodeProvider(Map<String, Object> configProperties) {
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.DCP_CLIENT_ID))) {
            throw new ConnectorException(GraphApiConstants.INVALID_CLIENT_ID);
        }
        final DeviceCodeCredential deviceCodeCredential = new DeviceCodeCredentialBuilder()
            .clientId(configProperties.get(GraphApiConstants.DCP_CLIENT_ID).toString())
            .challengeConsumer(challenge -> log.debug(challenge.getMessage())).build();

        // final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(deviceCodeCredential);
        // GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient()

        GraphServiceClient graphServiceClient = new GraphServiceClient(deviceCodeCredential);
        return GraphApiHelper.builder().graphClient(graphServiceClient).build();
    }

    private static GraphApiHelper interactiveProvider(Map<String, Object> configProperties) {
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.UIP_CLIENT_ID))) {
            throw new ConnectorException(GraphApiConstants.INVALID_CLIENT_ID);
        }
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.UIP_REDIRECT_URL))) {
            throw new ConnectorException("Redirect Url not Provided");
        }
        final InteractiveBrowserCredential interactiveBrowserCredential = new InteractiveBrowserCredentialBuilder()
            .clientId(configProperties.get(GraphApiConstants.UIP_CLIENT_ID).toString())
            .redirectUrl(configProperties.get(GraphApiConstants.UIP_REDIRECT_URL).toString()).build();

        // final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(interactiveBrowserCredential);
        // GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient()

        GraphServiceClient graphServiceClient = new GraphServiceClient(interactiveBrowserCredential);
        return GraphApiHelper.builder().graphClient(graphServiceClient).build();
    }

    private static GraphApiHelper userPasswordCredentials(Map<String, Object> configProperties) {
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.UCP_CLIENT_ID))) {
            throw new ConnectorException(GraphApiConstants.INVALID_CLIENT_ID);
        }
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.UCP_AUTH_USERNAME))) {
            throw new ConnectorException("Username not Provided");
        }
        if (!StringUtils.hasLength((String) configProperties.get(GraphApiConstants.UCP_AUTH_PASSWORD))) {
            throw new ConnectorException("Password not Provided");
        }
        final UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredentialBuilder()
            .clientId(configProperties.get(GraphApiConstants.UCP_CLIENT_ID).toString())
            .username(configProperties.get(GraphApiConstants.UCP_AUTH_USERNAME).toString())
            .password(configProperties.get(GraphApiConstants.UCP_AUTH_PASSWORD).toString()).build();

        // final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(usernamePasswordCredential);
        // GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider).buildClient()

        GraphServiceClient graphServiceClient = new GraphServiceClient(usernamePasswordCredential);
        return GraphApiHelper.builder().graphClient(graphServiceClient).build();
    }

}
