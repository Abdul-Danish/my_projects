/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.aws.s3;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.BasicAWSCredentials;
import com.digitaldots.connector.cache.Store;

@Component
public class AWSDS implements Store<BasicAWSCredentials, BasicAWSCredentials> {

    @Override
    public BasicAWSCredentials getDataSource(Map<String, Object> configProperties) {
        return new BasicAWSCredentials((String) configProperties.get("accessKey"), (String) configProperties.get("secretKey"));
    }

    @Override
    public String getDBTye() {
        return "AWSS3";
    }

    @Override
    public BasicAWSCredentials getConnection(BasicAWSCredentials datasource) {
        return datasource;
    }

}
