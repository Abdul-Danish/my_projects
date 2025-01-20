/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code.
 * DigitalDots Platform and associated code cannot be copied and/or distributed
 * without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

package com.digitaldots.connector.scraper;

class ScraperServiceTest {

//    @Autowired
//    static ConnectorRequest<?> connectorRequest;
//
//    ScraperService scraperService = new ScraperService();
//    ScraperBeverDS scraperBeverDS = new ScraperBeverDS();
//
//    @Test
//    void createJob() {
//        scraperService.scraperHelper = getDataSource();
//
//        scraperService.method = "job-create";
//        scraperService.solutionId = "6257bc1aa54d2d28f2a18aba";
//        scraperService.path = "/api/solutions/{solutionId}/projects/{project}/schedule";
//        String payload = "{\"alias\":\"test01\",\"properties\":{\"spider\":\"google_play_scrapy\",\"setting\":{\"DOWNLOAD_DELAY\":3,\"CONCURRENT_REQUESTS\":16,\"COOKIES_ENABLED\":true,\"ROBOTSTXT_OBEY\":true}}}";
//        scraperService.payload = new JSONObject(payload);
//        JSONObject response = scraperService.execute(connectorRequest);
//        assertThat(response).isNotNull();
//        System.out.println("\n" + response);
//    }
//
//    @Test
//    void getJobStatus() {
//        scraperService.scraperHelper = getDataSource();
//
//        scraperService.jobId = "625842829e4d831c525b596e";
//        scraperService.solutionId = "6257f8709e55345e3afbbe1a";
//        scraperService.method = "job-status";
//        scraperService.path = "/api/solutions/{solutionId}/projects/{project}/jobs/{jobId}/status";
//        JSONObject response = scraperService.execute(connectorRequest);
//        assertThat(response).isNotNull();
//        System.out.println("\n" + response);
//    }
//
//    @Test
//    void getJobResults() {
//        scraperService.scraperHelper = getDataSource();
//
//        scraperService.jobId = "625842829e4d831c525b596e";
//        scraperService.solutionId = "6257f8709e55345e3afbbe1a";
//        scraperService.method = "job-results";
//        scraperService.path = "/api/solutions/{solutionId}/projects/{project}/spiders/{spider}/jobs/{jobId}/items";
//        JSONObject response = scraperService.execute(connectorRequest);
//        assertThat(response).isNotNull();
//        System.out.println("\n" + response);
//    }
//
//    ScraperHelper getDataSource() {
//        scraperService.restTemplate = new RestTemplate();
//        // scraperService.mapper = new ObjectMapper();
//        scraperService.headers = getHeaders();
//
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put("appId", "com.babylon");
//        configProps.put("project", "test01");
//        configProps.put("spider", "google_play_scrapy");
//        // scraperBeverDS.basePath = "https://scrapers.bever.digitaldots.ai";
//        return scraperBeverDS.getDataSource(configProps);
//    }
//
//    Map<String, String> getHeaders() {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization",
//            "Bearer eyJhbGciOiJIUzUxMiJ9.eyJmaXJzdE5hbWUiOiJHaXRhbmphbGkiLCJsYXN0TmFtZSI6IlZlcm1hIiwic3ViIjoiZ2l0YW5qYWxpQGRpZ2l0YWxkb3RzLmFpIiwiZmlyc3RUaW1lTG9naW4iOmZhbHNlLCJyb2xlcyI6WyJTb2x1dGlvbiBEZXNpZ25lciJdLCJncm91cHMiOltdLCJpZCI6IjVmYzczMzU0M2EwYzI4MDgyMjljNGMyNyIsImF2YXRhciI6bnVsbCwiZXhwIjoxNjQ5OTU0ODE2LCJpYXQiOjE2NDk5NTEyMTYsInJlZnJlc2hUb2tlbiI6IjUyYzNlNjhmLTdlYTAtNDE2Ni05YjA3LTFmNTNjY2QzNzE3NiJ9.q5UJzAPPNFYHgqAMZcnuMh437HbFu2Del81hsCv_zfhYDAJTwFLrFN_iIXzz-CNAxzTlA1QcslQ-mBY2grPrHA");
//        return headers;
//    }
}