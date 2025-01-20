/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.aws.s3.impl.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.spi.ConnectorRequest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Connector(id = "AWSS3")
@Slf4j
@Getter
public class AWSS3Service {

    private static final int BAD_REQUEST_CODE = 400;
    private static final int SUCCESS_CODE = 200;

    @Connection
    protected BasicAWSCredentials authCredentials;

    @Params(name = "BucketName")
    protected String bucketName;

    @Params(name = "fileName")
    protected String fileName;

    @Params()
    protected String payload;

    @Params(name = "contentType")
    protected String contentType;

    @Execute
    public Object execute(ConnectorRequest<?> request) {
        log.debug("Executing AWS service");
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION)
                .withCredentials(new AWSStaticCredentialsProvider(authCredentials)).build();
            InputStream stream = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            s3Client.putObject(bucketName, fileName, stream, metadata);
            JSONObject response = new JSONObject();
            response.put("status", SUCCESS_CODE);
            response.put("message", "Success");
            response.put("data", new JSONArray());
            return response;

        } catch (RuntimeException e) {
            log.error("Exception Occured during executing AWS Service", e);
            JSONObject response = new JSONObject();
            response.put("status", BAD_REQUEST_CODE);
            response.put("message", "Not Supported");
            response.put("data", new JSONArray());
            return e;

        }
    }

}
