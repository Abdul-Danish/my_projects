package com.batch.process.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommaSeperator
//implements FunctionalInterfaceTest 
{

    public static void main(String[] args) throws JsonProcessingException {

//        System.out.println(UUID.randomUUID());
        
//		String topics = "ASSETS.STATUS BLUEPRINT.UPDATE BLUEPRINTS CLONE.SOLUTION DATASET.ANNOTATE.RESPONSE DELETE.SOLUTION EXPORT.BATCH.LAUNCH EXPORT.SOLUTION.COMPLETE EXPORT.SOLUTION.START IMPORT.BATCH.LAUNCH IMPORT.SOLUTION.START JOB.HISTORY.STATUS JOB.RESUME MANAGE.ENVIRONMENT MANAGE.SOLUTION MANAGE.SOLUTION-fdn-dlt MANAGE.SOLUTION-fdn-retry-0 MANAGE.SOLUTION-fdn-retry-1 MANAGE.SOLUTION-fdn-retry-2 MANAGE.SOLUTION-fdn-retry-3 MANAGE.SOLUTION-fdn-retry-4 MANAGE.SOLUTION-fdn-retry-5 MANAGE.SOLUTION-fdn-retry-6 MANAGE.SOLUTION-fdn-retry-7 MANAGE.SOLUTION-sol-dlt MANAGE.SOLUTION-sol-retry-0 MANAGE.SOLUTION-sol-retry-1 MANAGE.SOLUTION-sol-retry-2 MANAGE.SOLUTION-sol-retry-3 MANAGE.SOLUTION-sol-retry-4 MANAGE.SOLUTION-sol-retry-5 MANAGE.SOLUTION-sol-retry-6 MANAGE.SOLUTION-sol-retry-7 RULE.DEPLOYMENT SCHEDULE.CREATE SCHEDULE.HISTORY.DELETE SCHEDULE.RUN.DELETE SCHEDULE.STATUS SCHEDULE.UPDATE TEMPLATE.CREATE.REQUEST TEMPLATE.CREATE.RESPONSE USER.NOTIFICATIONS USER.TASK.NOTIFICATIONS __consumer_offsets asset_error_topic sandbox_DATASTORE.STATUS sandbox_DATASTORE.STATUS-sol-dlt sandbox_DATASTORE.STATUS-sol-retry-0 sandbox_DATASTORE.STATUS-sol-retry-1 sandbox_DATASTORE.STATUS-sol-retry-2 sandbox_DATASTORE.STATUS-sol-retry-3 sandbox_DATASTORE.STATUS-sol-retry-4 sandbox_DATASTORE.STATUS-sol-retry-5 sandbox_DATASTORE.STATUS-sol-retry-6 sandbox_DATASTORE.STATUS-sol-retry-7 sandbox_FUNCTION.STATUS sandbox_FUNCTION.STATUS-sol-dlt sandbox_FUNCTION.STATUS-sol-retry-0 sandbox_FUNCTION.STATUS-sol-retry-1 sandbox_FUNCTION.STATUS-sol-retry-2 sandbox_FUNCTION.STATUS-sol-retry-3 sandbox_FUNCTION.STATUS-sol-retry-4 sandbox_FUNCTION.STATUS-sol-retry-5 sandbox_FUNCTION.STATUS-sol-retry-6 sandbox_FUNCTION.STATUS-sol-retry-7 sandbox_MODEL.STATUS sandbox_MODEL.STATUS-sol-dlt sandbox_MODEL.STATUS-sol-retry-0 sandbox_MODEL.STATUS-sol-retry-1 sandbox_MODEL.STATUS-sol-retry-2 sandbox_MODEL.STATUS-sol-retry-3 sandbox_MODEL.STATUS-sol-retry-4 sandbox_MODEL.STATUS-sol-retry-5 sandbox_MODEL.STATUS-sol-retry-6 sandbox_MODEL.STATUS-sol-retry-7 sandbox_PUBLISH.STATUS sandbox_PUBLISH.STATUS-sol-dlt sandbox_PUBLISH.STATUS-sol-retry-0 sandbox_PUBLISH.STATUS-sol-retry-1 sandbox_PUBLISH.STATUS-sol-retry-2 sandbox_PUBLISH.STATUS-sol-retry-3 sandbox_PUBLISH.STATUS-sol-retry-4 sandbox_PUBLISH.STATUS-sol-retry-5 sandbox_PUBLISH.STATUS-sol-retry-6 sandbox_PUBLISH.STATUS-sol-retry-7";		
//		System.out.println(topics.replace(" ", ","));

//	    byte[] byteArray = {68, 69, 67, 73, 83, 73, 79, 78, 46, 82, 69, 83, 85, 76, 84};
//	    String str = new String(byteArray);
//
//	    System.out.println("Deserialized string: " + str);

//	    ObjectMapper objectMapper = new ObjectMapper();

//	    String temp = "{\"driverAge\":\"Senior Driver\",\"martialStatus\":\"Single\"}";
//	    Object convertValue = objectMapper.convertValue(temp, Object.class);
//	    System.out.println(convertValue);

        /*
         * LeetCode Problems
         */
        /*
         * // String s = "dabcabcdbb"; String s= "pwwkew";
         * 
         * List<String> splitString = new ArrayList<>(); List<String> letters = new ArrayList<>(); List<List<String>> result = new
         * ArrayList<>(); String longestString = ""; String letter = "";
         * 
         * boolean isDuplicate = false;
         * 
         * for (int i=0; i<s.length()-1; i++) { for (int j=0; j<=splitString.size()-1; j++) { // System.out.println(splitString); if
         * (splitString.contains(splitString.get(j))) { isDuplicate = true; } }
         * 
         * if (!isDuplicate) { longestString += s.charAt(i); splitString.add(String.valueOf(s.charAt(i))); } else { result.add(splitString);
         * splitString = new ArrayList<String>(); isDuplicate = false; } } System.out.println(result); int length = longestString.length();
         * System.out.println(longestString.substring(0, length-1));
         * 
         */

        /*
         * for (int i=0; i<s.length()-1; i++) { // a b c letters.add(String.valueOf(s.charAt(i))); System.out.println("letters: " +
         * letters); System.out.println("letter: " + letter); System.out.println("longestString: " + longestString); if
         * (!letters.contains(letter)) { longestString += String.valueOf(s.charAt(i)); if (s.length() >= i+2) { letter =
         * String.valueOf(s.charAt(i+2)); } } else { splitString.add(longestString); longestString = String.valueOf(s.charAt(i)); letters =
         * new ArrayList<String>(); } }
         */
//        System.out.println(splitString);

        /*
         * byte[] byteArray = {29, -95, -113, 8, 101, 63, 77, 80, -115, -62, -96, -85, 89, 34, 36, -38};
         * 
         * int[] intarray = new int[byteArray.length]; String stringId = new String(); for (byte b : byteArray) { stringId +=
         * String.valueOf(Byte.toUnsignedInt(b)) + ","; // System.out.println(Byte.toUnsignedInt(b)); }
         * System.out.println(stringId.substring(0, stringId.length()-1));
         * 
         * // [29, 161, 143, 8, 101, 63, 77, 80, 141, 194, 160, 171, 89, 34, 36, 218]
         * 
         * String[] listIds = stringId.split(","); List<Byte> stringToByte = new ArrayList<>(); for (String string : listIds) { int i =
         * Integer.valueOf(string); byte b = (byte) i; stringToByte.add(b); } System.out.println(stringToByte);
         * 
         * }
         */

        /*
         * public void test(String workflowXml, String type) { DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         * try { DocumentBuilder builder = builderFactory.newDocumentBuilder(); Document doc = builder.parse(new
         * ByteArrayInputStream(workflowXml.getBytes())); Element element = doc.getDocumentElement(); element.removeAttributeNS(workflowXml,
         * type); } catch (ParserConfigurationException | SAXException | IOException e) { e.printStackTrace(); } }
         */

//	FunctionalInterfaceTest interfaceTest = str -> "ok";
//	System.out.println(interfaceTest.print("display"));
//	
//	
//	CommaSeperator commaSeperator = new CommaSeperator();
//	System.out.println(commaSeperator.print("?"));

//	new CommaSeperator() {
//	    str -> "dmo";
//	};

        String data = "{\"data\":1,\"message\":\"ACCEPTED\",\"status\":200}";
//	    Map obj = new HashMap<>();
//	    obj.put("data", 1);
//	    obj.put("message", "ACCEPTED");
//	    obj.put("status", 200);
//	    
//	    ObjectMapper objectMapper = new ObjectMapper();
//	    JSONObject response = objectMapper.convertValue(objectMapper.writeValueAsString(obj), JSONObject.class);
//	    System.out.println(response);
        
//        Object data2 = getData();
//        System.out.println(data2);
        
//        String response = "1";
//        Map readValue = new ObjectMapper()
//            .readValue(response.toString(), Map.class);
//        System.out.println(readValue);
        
        
        String key = "-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDBsWS8DJ1pSsIG\\nAWJqMQduvNOfMXLcHniylH06gFfgBnBW1ozQJRguRHi19lQ2iAqKTmUvmLjWXLlZ\\ncluHTk1c8Y4EEluOpdzWXq1020AelNf1FsFAX7EftXciMoGSTWWCumWGyeAgNWlW\\nDmUETHqDgwPTQVuJEMrzCjri/4bFHWZuKIaftebNaLLrvxN9ddQH4COyXb+PGXuX\\n665ZH0nfWJdthCgjUT5bQEe+QvGFZ6qmgNjoSu7WxncBvSlSGnXIGWJim9qh0uAV\\nnCVE/ZkgBVJfcNrYOAVkFDku8yDODqEddfxen/Dwj23s6p+7m3xHsxyuxehTva26\\n8TIawTn3AgMBAAECggEAUaJ3/+/bKkT5WHmd5n7KkKgiEx7Ot6paGX4O96ifIMrl\\n7xuXFqUDt4BhVeO/o4ob0yXubKyT/rju352cqP7eCVW/ksS/9PPAC1O1DKLZt+no\\nySHxijWqm+AqGW4rASQArCPuZWxRO5H3Vu9TMQggvKvr4U5TC4ZZ2yxNHF4xCYM6\\nOIMhBSUQ0ufQA7K3+QIs7vwfZp31/P4hs4o/PNTkjomePL9w1vP8yx90LNYwNpst\\n6omAvMje/rVcugKZTZaBxGelMYGSbO1rp7uRN7X2z0B4DBmuxXPpffM0sSNjjoRK\\nEv5XjCV2SbYpIucgf8r0yI/yDQ23ilmYHkHiViz9vQKBgQDqBZKfcqV/IUWKc12Z\\nuDUxdhuuXeWt2VpkkwzItmOOyDjcBTrL/8NQtPh5QasrSoUdOimY2D+G0jaLrPgq\\nVn5wcbnmooa0OidFUmXU4wzqEK6DB+hgyicuzxNybkiaTQXrzsmXnibcb8l5CDdm\\nB+d5T9ECGNDK8CrOtg5KAd+M5QKBgQDT4jjYj/DCWijLOl2O7EZNlDyifvu3sFxM\\ninw/vDXSM3psRJfiKW6YVwMtiI8uq4RejozfiEUX4U2IO5Iwlr77S9JvbUrfmdqn\\nS9mvGuS/C6N2xmDCZDwQuh2P2X12PnP+0wE16SfH0kl8o3ysjISTl8U8tzkWJAHE\\n+OvRLADZqwKBgQCD8/p1r3ZDlaYZZ+1aFLThm8AF9GniOdEvLn8h2T2Pr7Pn04cQ\\nqbkek7wa2v1B3rXqAfaceSpwwa0B2tjfPn/ytR1mPzQHAVdNTiWfARsyC4/q0BWm\\nJbYsPZSwjCCh6FYzXRjsRb+RwfJvLUPXYxOQooGuVgG8u+jXP24VKrM7RQKBgQCc\\nhJQxhcL4DtnrpmXOWkNks4hHET6o5qKH+BTokAPCDzz0FYeNDcYgysYSMLp0Y0cZ\\nAnyV83f2t/wqErdfJTxXLh95KGcS3fhjdOiNLXSkm9hYuRpo/tpQEOwdgy/m1SOi\\nrgRK6rz0Iycd5zcFz5dv38FXpJGLBXY5JxgsIDFQmQKBgQDBFx1w2vt827yphv5h\\nmbGzt+BOfCttymLDDKYayQEiw8Et0WTzuV+qQQ1xYq80Z3/uOAZsqgWZFaS/n9Qh\\n7G6vyrMuACLBCWVV0Gu7iemGc/oS+l/EL2XhYBMaRVz8UbHgEAaMWbUI2gjSo6t8\\nL1GjhvyRMhh3e5c5TvYR4ZjWAA==\\n-----END PRIVATE KEY-----\\n";
        
        
        System.out.println(key.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "").replace("\\n", ""));
        
        
        
        
        
        
        
        
        
        
        
    }

    public static Object getData() throws JsonProcessingException, IllegalArgumentException {
        Map obj = new HashMap<>();
        obj.put("\"data\"", 1);
        obj.put("\"message\"", "ACCEPTED");
        obj.put("\"status\"", 200);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        objectMapper.findAndRegisterModules();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        JSONObject response = objectMapper.convertValue(objectMapper.writeValueAsString(obj), JSONObject.class);
        System.out.println("Res " + response);
        return Objects.nonNull(response.get("data")) ? response.get("data") : response;
    }

    public void rtnSample() {
        FunctionalInterfaceTest functionalInterfaceTest = new FunctionalInterfaceTest() {
            @Override
            public void rtn(String str) {
                System.out.println("Tst");
            }
        };
    }

//    @Override
//    public String print(String str) {
//        return "Tst";
//    }
}
