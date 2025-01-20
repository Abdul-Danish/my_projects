/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.sql;

class SqlServiceTest {

//    private static final String jdbcUrl = "jdbc:postgresql://localhost:5432/test?user=postgres&password=password&ssl=false";
//    private static final String jdbcUrl = "jdbc:mysql://Rahul:PaassW0rd$@localhost:3306/test";
//
//    @Autowired
//    static ConnectorRequest<?> connectorRequest;
//
//    @Autowired
//    Connection connection;
//
//    @Test
//    void fetch() throws SQLException, JsonMappingException, JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("jdbcUrl", jdbcUrl);
//        properties.put("authUserName", "postgres");
//        properties.put("authPassword", "passwSqlord");
//
//        SqlBeverDS sqlBeverDs = new SqlBeverDS();
//        NamedParameterJdbcTemplate jdbcTemplate = sqlBeverDs.getDataSource(properties);
//        List<JSONObject> paramsList = new ArrayList<>();
//        JSONObject params = new JSONObject();
//
//        params.put("employeeid", 1);
//        paramsList.add(params);
//        JsonNode paramsNode = mapper.readTree(params.toString());
//
//        SqlService sqlService = new SqlService();
//        sqlService.jdbcTemplate = jdbcTemplate;
//        sqlService.query = "SELECT * FROM email WHERE employeeid =:employeeid";
//        sqlService.params = paramsNode;
//        sqlService.method = "SELECT";
//        System.out.println("OUTPUT " + sqlService.execute(null));
//    }
//
//    @Test
//    void insert() throws SQLException {
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("jdbcUrl", jdbcUrl);
//        properties.put("authUserName", "Rahul");
//        properties.put("authPassword", "PaassW0rd$");
//
//        SqlBeverDS sqlBeverDs = new SqlBeverDS();
//        NamedParameterJdbcTemplate jdbcTemplate = sqlBeverDs.getDataSource(properties);
//        List<JSONObject> paramsList = new ArrayList<>();
//        JSONObject params = new JSONObject();
//        params.put("address", "test21@test.com");
//        params.put("employeeid", 21);
//        paramsList.add(params);
//        JsonNode paramsNode = mapper.convertValue(params.toString(), JsonNode.class);
//
//        SqlService sqlService = new SqlService();
//        sqlService.jdbcTemplate = jdbcTemplate;
//        sqlService.query = "INSERT INTO email(employeeid,address) VALUES(:employeeid,:address)";
//        sqlService.params = paramsNode;
//        sqlService.method = "INSERT";
//        System.out.println("OUTPUT " + sqlService.execute(null));
//    }
//
//    @Test
//    void update() throws SQLException {
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("jdbcUrl", jdbcUrl);
//        properties.put("authUsername", "Rahul");
//        properties.put("authPassword", "PaassW0rd$");
//
//        SqlBeverDS sqlBeverDs = new SqlBeverDS();
//        NamedParameterJdbcTemplate jdbcTemplate = sqlBeverDs.getDataSource(properties);
//        List<JSONObject> paramsList = new ArrayList<>();
//        JSONObject params = new JSONObject();
//        params.put("address", "test12@test.com");
//        params.put("employeeid", 12);
//        paramsList.add(params);
//        JsonNode paramsNode = mapper.convertValue(params.toString(), JsonNode.class);
//
//        SqlService sqlService = new SqlService();
//        sqlService.jdbcTemplate = jdbcTemplate;
//        sqlService.query = "UPDATE email SET employeeId=88, address='doe@digitaldots.ai' WHERE address=:address";
//        sqlService.params = paramsNode;
//        sqlService.method = "UPDATE";
//        System.out.println("OUTPUT " + sqlService.execute(null));
//    }
//
//    @Test
//    void delete() throws SQLException {
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("jdbcUrl", jdbcUrl);
//        properties.put("authUserName", "Rahul");
//        properties.put("authPassword", "PaassW0rd$");
//
//        SqlBeverDS sqlBeverDs = new SqlBeverDS();
//        NamedParameterJdbcTemplate jdbcTemplate = sqlBeverDs.getDataSource(properties);
//        List<JSONObject> paramsList = new ArrayList<>();
//        JSONObject params = new JSONObject();
//        params.put("employeeid", 12);
//        paramsList.add(params);
//        JsonNode paramsNode = mapper.convertValue(params.toString(), JsonNode.class);
//
//        SqlService sqlService = new SqlService();
//        sqlService.jdbcTemplate = jdbcTemplate;
//        sqlService.query = "DELETE FROM email where employeeid=:employeeid";
//        sqlService.params = paramsNode;
//        sqlService.method = "DELETE";
//        System.out.println("OUTPUT " + sqlService.execute(null));
//    }
//
//    @Test
//    void executeBatch() throws SQLException {
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("jdbcUrl", jdbcUrl);
//        properties.put("authUserName", "Rahul");
//        properties.put("authPassword", "PaassW0rd$");
//
//        SqlBeverDS sqlBeverDs = new SqlBeverDS();
//        NamedParameterJdbcTemplate jdbcTemplate = sqlBeverDs.getDataSource(properties);
//        List<JSONObject> paramsList = new ArrayList<>();
//        JSONObject params = new JSONObject();
//        params.put("employeeid", 12);
//        params.put("address", "test12@test.com");
//        paramsList.add(params);
//        JSONObject params2 = new JSONObject();
//        params2.put("employeeid", 21);
//        params2.put("address", "test21@test.com");
//        paramsList.add(params2);
//        JsonNode paramsNode = mapper.convertValue(paramsList.toString(), JsonNode.class);
//        System.out.println("PARAMS NODE " + paramsNode);
//
//        SqlService sqlService = new SqlService();
//        sqlService.jdbcTemplate = jdbcTemplate;
//        sqlService.query = "INSERT INTO email(employeeid,address) VALUES(:employeeid,:address)";
//        sqlService.params = paramsNode;
//        sqlService.method = "BATCH";
//        System.out.println("OUTPUT " + sqlService.execute(null));
//    }

}
