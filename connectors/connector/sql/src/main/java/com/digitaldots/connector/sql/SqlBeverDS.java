/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.cache.Store;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SqlBeverDS implements Store<NamedParameterJdbcTemplate, NamedParameterJdbcTemplate> {

    private static final int MIN_POOL_SIZE = 20;
    private static final int PREP_STMT_CACHE_LMT = 256;
    private static final int PREP_STMT_CACHE_SIZE = 25;
    private static final int MIN_IDLE_TIME = 5;
    private static final int MAX_LIFE_TIME = 1_800_000;
    private static final int IDLE_TIME_OUT = 60_000;
    private static final int DEFAULT_CONNECTION_TIME_OUT = 30000;
    private static final String JDBC_URL = "jdbcUrl";
    private static final String USERNAME = "authUserName";
    private static final String PASSWORD = "authPassword";

    private void checkNull(Map<String, Object> configProperties) {
        if (Objects.isNull(configProperties.get(JDBC_URL))) {
            throw new ConnectorException("Jdbc Url cannot be null");
        }
        if (Objects.isNull(configProperties.get(USERNAME))) {
            throw new ConnectorException("username cannot be null");
        }
        if (Objects.isNull(configProperties.get(PASSWORD))) {
            throw new ConnectorException("password cannot be null");
        }
    }

    @Override
    public NamedParameterJdbcTemplate getDataSource(Map<String, Object> configProperties) {
        Map<String, String> props = new HashMap<>();
        checkNull(configProperties);
        props.put(JDBC_URL, (String) configProperties.getOrDefault(JDBC_URL, ""));
        props.put("dataSource.dataSourceClassName", (String) configProperties.getOrDefault("dataSourceClassName", ""));
        props.put("dataSource.user", (String) configProperties.get(USERNAME));
        props.put("dataSource.password", (String) configProperties.get(PASSWORD));
        props.put("dataSource.connectionTimeout",
            configProperties.getOrDefault("connectionTimeOut", DEFAULT_CONNECTION_TIME_OUT).toString());
        props.put("dataSource.idleTimeout", configProperties.getOrDefault("idleTimeout", IDLE_TIME_OUT).toString());
        props.put("dataSource.maxLifetime", configProperties.getOrDefault("maxLifetime", MAX_LIFE_TIME).toString());
        props.put("dataSource.minimumIdle", configProperties.getOrDefault("minimumIdle", MIN_IDLE_TIME).toString());
        props.put("dataSource.maximumPoolSize", configProperties.getOrDefault("maximumPoolSize", MIN_POOL_SIZE).toString());
        if (Boolean.TRUE.equals(configProperties.getOrDefault("cachePrepStmts", Boolean.FALSE))) {
            props.put("dataSource.prepStmtCacheSize", configProperties.getOrDefault("prepStmtCacheSize", PREP_STMT_CACHE_SIZE).toString());
            props.put("dataSource.prepStmtCacheSqlLimit",
                configProperties.getOrDefault("prepStmtCacheSqlLimit", PREP_STMT_CACHE_LMT).toString());

        }
        Properties properties = new Properties();
        properties.putAll(props);
        log.debug("Connection Pool properties are {}", properties);
        HikariConfig config = new HikariConfig(properties);
        log.debug("Jdbc url is {}", config.getJdbcUrl());
        HikariDataSource dataSource = new HikariDataSource(config);
        log.debug(dataSource.getJdbcUrl());
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public String getDBTye() {
        return "SQL";
    }

    @Override
    public NamedParameterJdbcTemplate getConnection(NamedParameterJdbcTemplate jdbcTemplate) {
        return jdbcTemplate;
    }

    @Override
    public String validate(NamedParameterJdbcTemplate jdbcTemplate, Map<String, Object> properties) {
        try {
            jdbcTemplate.execute(properties.get("testQuery").toString(), new PreparedStatementCallback<Object>() {
                public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                    log.debug("prepared statement ", ps);
                    return null;
                }
            });
            log.debug("SQL connection is validated");
            return "Successfully Connected";
        } catch (Exception e) {
            log.error("Exception Occured while validating SQL connection {}", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
