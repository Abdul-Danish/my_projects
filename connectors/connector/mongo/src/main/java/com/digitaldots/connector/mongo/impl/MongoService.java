/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.mongo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.digitaldots.connector.ConnectorException;
import com.digitaldots.connector.annotation.Connection;
import com.digitaldots.connector.annotation.Connector;
import com.digitaldots.connector.annotation.Execute;
import com.digitaldots.connector.annotation.Params;
import com.digitaldots.connector.mongo.MongoHelper;
import com.digitaldots.connector.spi.ConnectorRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;

import lombok.extern.slf4j.Slf4j;

@Connector(id = "MongoDB")
@Slf4j
public class MongoService {

    @Connection
    protected MongoHelper mongoHelper;

    @Params(name = "method")
    protected String dbOperation;

    @Params(name = "filter")
    protected JSONObject filter;

    @Params()
    protected String payload;

    @Params(name = "table")
    protected String table;

    @Params(name = "defaultDatabase")
    protected String db;

    @Params(name = "project")
    protected JSONObject fields;

    @Params(name = "limit")
    protected Integer limit;

    @Params(name = "skip")
    protected Integer skip;

    @Value("${mongo.query.limit:10}")
    protected Integer queryLimit;

    @Value("${mongo.query.skip:0}")
    protected Integer queryskip;

    @Autowired
    private ObjectMapper objectMapper;

    @Execute
    public Object execute(ConnectorRequest<?> request) {
        try {
            MongoDatabase database = mongoHelper.getDatabase(db);
            log.debug("DataBase Name is {}", database.getName());
            log.debug("performing db {} operation ", dbOperation);
            MongoCollection<Document> collection = database.getCollection(table);
            Object response = null;
            switch (dbOperation) {
            case "SELECT":
                response = retrieve(collection, filter, fields);
                break;
            case "INSERT":
                response = insert(collection, payload);
                break;
            case "UPDATE":
                response = update(collection, filter, payload);
                break;
            case "DELETE":
                response = delete(collection, filter);
                break;
            default:
                throw new ConnectorException("Invalid method exception from mongo connector");
            }
            return response;
        } catch (Exception e) {
            log.error("Exception occured while executing Mongo Service", e);
            throw new ConnectorException("exception from mongo connector" + e.getMessage());
        }
    }

    private JSONArray retrieve(MongoCollection<Document> collection, JSONObject filter, JSONObject fields) {
        JSONArray data = new JSONArray();
        BasicDBObject dbQuery = new BasicDBObject();
        log.debug("query is NEW {} ", filter);
        log.debug("dbFilter is {} ", fields);
        if (Objects.nonNull(filter)) {
            dbQuery = BasicDBObject.parse(filter.toString());
        }
        BasicDBObject dbFilter = new BasicDBObject();
        if (Objects.nonNull(fields)) {
            dbFilter = BasicDBObject.parse(fields.toString());
        }
        if (Objects.isNull(limit)) {
            limit = queryLimit;
        }
        if (Objects.isNull(skip)) {
            skip = queryskip;
        }
        MongoCursor<Document> cursor = collection.find(dbQuery).limit(limit).skip(skip).projection(dbFilter).iterator();
        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                ObjectId id = document.getObjectId("_id");
                if (Objects.nonNull(id)) {
                    document.put("_id", id.toString());
                }
                Map<String, Object> obj = new HashMap<>();
                for (Map.Entry<String, Object> doc : document.entrySet()) {
                    obj.put(doc.getKey(), doc.getValue());
                }
                if (!obj.isEmpty()) {
                    data.put(obj);
                }
            }
        } finally {
            cursor.close();
        }
        return data;
    }

    private Object insert(MongoCollection<Document> collection, String payload) throws JsonProcessingException {

        if (Objects.isNull(payload)) {
            throw new ConnectorException("Bad Request");
        } else if (payload.startsWith("{")) {
            Document document = Document.parse(payload);
            if (Objects.nonNull(document.get("_id"))) {
                ObjectId id = new ObjectId(document.get("_id", ""));
                document.put("_id", id);
            } else {
                document.put("_id", ObjectId.get());
            }
            InsertOneResult result = collection.insertOne(document);
            if (result.wasAcknowledged()) {
                document.put("id", document.get("_id"));
                document.remove("_id");
                return document.toJson();
            }
        } else {
            JSONArray list = new JSONArray(payload);
            if (list.length() == 0) {
                log.warn("No records to update {}", list);
                return list;
            }
            List<Document> documentsList = new ArrayList<>();
            for (Object hashMap : list) {
                Document document = Document.parse(hashMap.toString());
                documentsList.add(document);
            }
            InsertManyResult result = collection.insertMany(documentsList);
            // result.
            if (result.wasAcknowledged()) {
                return objectMapper.writeValueAsString(documentsList);
            }
        }
        throw new ConnectorException("Unable to process the request.");
    }

    public JSONObject update(MongoCollection<Document> collection, JSONObject queryFilter, String payload) {
        Document responseDocument;
        JSONObject response = new JSONObject();
        BasicDBObject dbQuery = null;
        if (Objects.nonNull(queryFilter)) {
            dbQuery = BasicDBObject.parse(queryFilter.toString());
            if (Objects.nonNull(queryFilter.get("_id"))) {
                dbQuery.computeIfPresent("_id", (k, v) -> new ObjectId((String) v));
            }
        } else {
            dbQuery = new BasicDBObject();
        }

        if (Objects.isNull(queryFilter)) {
            throw new ConnectorException("Bad Request.  Filter criteria is mandatory");
        } else if (Objects.isNull(payload)) {
            throw new ConnectorException("Bad Request.  payload is mandatory");
        } else {
            try {
                responseDocument = collection.findOneAndUpdate(dbQuery, new Document("$set", BsonDocument.parse(payload)));
                response.put("message", responseDocument.size() + " records Updated");
                return new JSONObject();
            } catch (RuntimeException e) {
                log.error("Exception Occured while updating", e);
                throw new ConnectorException("Bad Request.  payload is mandatory");
            }
        }
    }

    private JSONObject delete(MongoCollection<Document> collection, JSONObject queryFilter) {

        if (Objects.isNull(queryFilter)) {
            throw new ConnectorException("Bad Reuest.  Filter criteria is mandatory");
        } else {
            BasicDBObject dbQuery = BasicDBObject.parse(queryFilter.toString());
            DeleteResult deleteResult = collection.deleteMany(dbQuery);
            log.debug(" {} records Deleted", deleteResult.getDeletedCount());
            return new JSONObject();
        }
    }
}
