/**
 * Copyright (C) 2019-2022 Evolve. Platform, DigitalDots Inc.
 * 
 * This file is part of DigitalDots Evolve Platform Source Code. DigitalDots Platform and associated code cannot be copied and/or
 * distributed without a written consent from DigitalDots Inc. and/or its subsidiaries.
 */

//package com.digitaldots.connector;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.jupiter.api.Test;
//
//import com.digitaldots.connector.ad.service.ADService;
//import com.digitaldots.connector.graphapi.service.GraphApiBeverDS;
//import com.digitaldots.connector.graphapi.util.GraphApiConstants;
//import com.digitaldots.connector.graphapi.util.GraphApiHelper;
//
//class ADTest {
//
//	private String userMail = "cric@digitaldots.ai";
//
//	@Test
//	void createUser() {
//		GraphApiHelper graphApiHelper = getGraphApiHelper();
//		ADService adService = new ADService();
//		adService.graphApiHelper = graphApiHelper;
//		adService.directory = "vendors";
//		List<String> groups = new ArrayList<String>();
//		groups.add("Test Group");
//		groups.add("AB@()\\[]\";:.<>,C");
//		adService.groups = groups;
//		Map<String, String> user = new HashMap<>();
//		user.put("userPrincipalName", userMail);
//		user.put("preferredName", "Demo User");
//		user.put("displayName", "Demo User");
//		user.put("mailNickname", "DemoUser");
//		adService.userDetails = user;
//		adService.method = "create";
//		System.out.println(adService.execute(null));
//	}
//
//	@Test
//	void updateUser() {
//		GraphApiHelper graphApiHelper = getGraphApiHelper();
//		ADService adService = new ADService();
//		adService.graphApiHelper = graphApiHelper;
//		adService.method = "update";
//		adService.directory = "vendor";
//		List<String> groups = new ArrayList<String>();
//		groups.add("Test");
//		adService.groups = groups;
//		Map<String, String> user = new HashMap<>();
//		user.put("userPrincipalName", userMail);
//		user.put("preferredName", "Updated");
//		user.put("displayName", "Updated");
////		adService.graphServiceClient = gc;
//		adService.userDetails = user;
//		System.out.println(adService.execute(null));
//	}
//
//	@Test
//	void deleteUser() {
//		GraphApiHelper graphApiHelper = getGraphApiHelper();
//		ADService adService = new ADService();
//		adService.graphApiHelper = graphApiHelper;
//		adService.method = "delete";
//		Map<String, String> user = new HashMap<>();
//		user.put("userPrincipalName", userMail);
//		adService.userDetails = user;
//		System.out.println(adService.execute(null));
//	}
//
//	private GraphApiHelper getGraphApiHelper() {
//	    GraphApiBeverDS graphApiBeverDS = new GraphApiBeverDS();
//	    
//	    Map<String, Object> configProperties = new HashMap<>();
//	    configProperties.put("authMethod",GraphApiConstants.CLIENT_CREDENTIALS_PROVIDER);
//        configProperties.put(GraphApiConstants.CCP_CLIENT_ID, "83b86247-94fa-4c70-beff-f69c35653bed");
//        configProperties.put(GraphApiConstants.CCP_CLIENT_SECRET, "2GQ~.r_tr6K__7nKRh.eEv3J8Ao2K981hm");
//        configProperties.put(GraphApiConstants.CCP_TENANT_ID, "09d9df66-37d7-493b-8823-4af2c12a1af5");
//        return graphApiBeverDS.getDataSource(configProperties);
//
//	}
//}
