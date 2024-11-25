package com.xslt.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.TransformerException;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import ch.qos.logback.core.net.SyslogOutputStream;
import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Run {

    public static void main(String[] args) throws IOException, TransformerException {

        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:/postman/platform_jsons/" + "DD-Foundation.postman_collection.json");
//        Resource resource = resourceLoader.getResource("classpath:/postman/jsons/" + "petstore_postman_collection_Mod.json");
//        FileInputStream fileInputStream = new FileInputStream(new File("/home/digitaldots/Documents/DigitalDots/XSLT/src/main/resources/yaml_files/pet_store_swagger.yaml"));

        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readValue(readFromInputStream(resource.getInputStream()), JsonNode.class);
        
        /*
         * json pre-processing for postman api's
         * 
         */
        
        String inputPayload = readFromInputStream(resource.getInputStream());
//        removeDuplicateUrls(convertValue);
        
        
        JsonNode jsonNode = null;
        jsonNode = objectMapper.readValue(inputPayload, JsonNode.class);
        
//        List<Map<String, Object>> convertValue = objectMapper.convertValue(jsonNode.get("item"), new TypeReference<List<Map<String, Object>>>() {});
//        List<Map<String, Object>> result = removeDuplicateUrls(convertValue, new HashSet<>(), objectMapper);
//        JsonNode convertedNode = objectMapper.convertValue(result.get(0), JsonNode.class);
//        System.out.println("Res: " + convertedNode.toPrettyString());
        
        
        
//        System.out.println(convertValue);
        
//        JsonNode res = addNameIfMissing(jsonNode);
//        JsonNode res = addPathAndQueryIfMissing(addedName);
        
        JsonNode res = removeNodesWithDuplicateUrl(jsonNode, jsonNode, new HashSet<>(), objectMapper);
        System.out.println(res.toPrettyString());
        
        
        /*
         * json pre-processing for swagger api's
         * 
        String inputPayload = readFromInputStream(resource.getInputStream());
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        JsonNode jsonNode = null;
        if (inputPayload.startsWith("{")) {
            jsonNode = objectMapper.readValue(inputPayload, JsonNode.class);
        } else {
            jsonNode = yamlMapper.readValue(inputPayload, JsonNode.class);
        }
        
        Set<String> tagsSet = new HashSet<>();
        List<Map<String, String>> tagsList = new ArrayList<>();

        if (jsonNode.has("tags")) {
            JsonNode tagNode = jsonNode.path("tags");
            Iterator<JsonNode> elements = tagNode.elements();
            while (elements.hasNext()) {
                JsonNode name = elements.next().get("name");
                System.out.println(name);
                Map<String, String> tgMap = new HashMap<>();
                tgMap.put("name", name.asText());
                tagsList.add(tgMap);
                tagsSet.add(name.asText());
            }
        }

        JsonNode pathNode = jsonNode.path("paths");
        
        Iterator<Entry<String, JsonNode>> fields = pathNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> next = fields.next();
            Iterator<JsonNode> elements = next.getValue().elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                if (!element.path("tags").isArray() || element.path("tags").size()==0) { 
                    ((ObjectNode) element).putArray("tags").add("default");
                }
                JsonNode tagsNode = element.path("tags");
                if (tagsNode.isArray()) {
                    for (JsonNode tag : tagsNode) {
                        if (!tagsSet.contains(tag.asText())) {
                            Map<String, String> tagsMap = new HashMap<>();
                            tagsSet.add(tag.asText());
                            tagsMap.put("name", tag.asText());
                            tagsList.add(tagsMap);
                        }
                    }
                }
            }
        }
        JsonNode listNode = objectMapper.convertValue(tagsList, JsonNode.class);
        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.put("tags", listNode);

        System.out.println(objectNode.toPrettyString());
        */
        
        

    }
    
//    private static JsonNode addNameIfMissing(JsonNode jsonNode) {
//        if (jsonNode.isObject()) {
//            ObjectNode objectNode = (ObjectNode) jsonNode;
//            if (objectNode.has("item")) {
//                JsonNode itemNode = objectNode.get("item");
//                Iterator<JsonNode> elements = itemNode.elements();
//                while(elements.hasNext()) {
//                    JsonNode names = elements.next();
//                    for (JsonNode name : names) {
//                        if (!name.has("name")) {
//                            System.out.println(name);
////                            ((ObjectNode) name).put("name", "default");
//                        }
//                        addNameIfNotExist(name);
//                    }
//                }
//                
////                if (itemNode.isArray()) {
////                }
//            }
//        }        
//        return jsonNode;
//    }
    
    /*
    private static Map<String, List<String>> removeNodesWithDuplicateUrlTemp(JsonNode node, Map<String, List<String>> urlMethodMap) {        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = (ObjectNode) node;
//        Map<String, String> urlMethodMap = new HashMap<>();
        if (objectNode.has("item")) {
            JsonNode itemNode = objectNode.get("item");
            if (itemNode.isArray()) {
                for (JsonNode item : itemNode) {
                    JsonNode requestNode = item.get("request");
                    if (requestNode != null) {
//                        getMethodsForUrl(itemNode, requestNode.get("url").asText(), new ArrayList<>());
                        if(urlMethodMap.get(requestNode.get("url").asText()) == null) {
                            urlMethodMap.put(requestNode.get("url").asText(), new  ArrayList<>(Arrays.asList(requestNode.get("method").asText())));
                        } else {
                            List<String> list = urlMethodMap.get(requestNode.get("url").asText());
                            // Add method to the url if does not exist
                            if (!list.contains(requestNode.get("method").asText())) {
                                list.add(requestNode.get("method").asText());
                                urlMethodMap.replace(requestNode.get("url").asText(), list);
                                
                                JsonNode methodNode = requestNode.get("method");
                                System.out.println(methodNode);
                                ArrayNode arrayNode = mapper.createArrayNode();
                                for (String method : list) {                                    
                                    arrayNode.add(method);
                                }
//                                ObjectNode methodObjNode = (ObjectNode) methodNode;
//                                methodNode.re
                                ((ObjectNode) requestNode).put("method", arrayNode);
                            }
                        }
                        
                    }
                    removeNodesWithDuplicateUrl(item, urlMethodMap);
                }
            }
        }
//        System.out.println("MAP: " + urlMethodMap);
        System.out.println(node.toPrettyString());
        return urlMethodMap;
    }
    */
    

    /*
    public static JsonNode removeNodesWithDuplicateUrl(JsonNode jsonNode, JsonNode node, Set<String> urlsSet, ObjectMapper mapper) {
        ObjectNode objectNode = (ObjectNode) node;
//        Map<String, String> urlMethodMap = new HashMap<>();
//        if (objectNode.has("item")) {
            JsonNode itemNode = objectNode.get("item");
            if (itemNode != null && itemNode.isArray()) {
                for (JsonNode item : itemNode) {
                    JsonNode requestNode = item.get("request");
                    if (requestNode != null) {
                        JsonNode urlNode = requestNode.get("url");
                        if (urlNode != null) {
                            String url = urlNode.has("raw") ? urlNode.get("raw").asText() : urlNode.asText();
                            Map<String, Object> methodsAndHeadersForUrl = getMethodsAndHeadersForUrl(jsonNode, url, new ArrayList<>(), new HashMap<String, Object>());
                            
                            List<String> methodsForUrl = mapper.convertValue(methodsAndHeadersForUrl.get("methods"), new TypeReference<List<String>>() {
                            });
                            JsonNode headersForUrl = mapper.convertValue(methodsAndHeadersForUrl.get("headers"), JsonNode.class);
//                        System.out.println(methodsForUrl);
                            if (!urlsSet.contains(url)) {
                                urlsSet.add(url);
                                if (methodsForUrl.size() > 1) {
                                    ArrayNode methodNode = mapper.createArrayNode();
                                    for (String method : methodsForUrl) {
                                        methodNode.add(method);
                                    }
                                    ArrayNode headerNode = mapper.createArrayNode();
                                    headerNode.add(headersForUrl);
                                    ((ObjectNode) requestNode).remove("method");
                                    ((ObjectNode) requestNode).set("method", methodNode);
                                    ((ObjectNode) requestNode).remove("header");
                                    ((ObjectNode) requestNode).set("header", headerNode);
                                }
                            } else {
                                System.out.println(url);
                                ((ObjectNode) requestNode).removeAll();
                            }

                        }
                    }
                    removeNodesWithDuplicateUrl(jsonNode, item, urlsSet, mapper);
                }
            }
//        }
//        System.out.println("MAP: " + urlMethodMap);
//        System.out.println(node.toPrettyString());
        return node;
    }
        
    private static Map<String, Object> getMethodsAndHeadersForUrl(JsonNode jsonNode, String url, ArrayList<String> methods, Map<String, Object> headersList) {
        if (jsonNode.has("item")) {
            JsonNode itemNode = jsonNode.get("item");
            for (JsonNode item : itemNode) {
                JsonNode requestNode = item.get("request");
                if (requestNode != null && requestNode.get("url") != null) {
                    JsonNode urlNode = requestNode.get("url");
                    String currentUrl = urlNode.has("raw") ? urlNode.get("raw").asText() : urlNode.asText();
                    if (currentUrl.equals(url) && requestNode.get("method") != null
                        && !methods.contains(requestNode.get("method").asText())) {
                        methods.add(requestNode.get("method").asText());

                        if (requestNode.has("header") && !requestNode.get("header").isEmpty()  && !requestNode.get("method").asText().isEmpty()) {
                            JsonNode headerNode = requestNode.get("header");
                            Iterator<JsonNode> elements = headerNode.elements();
                            List<Map<String, String>> headerMapList = new ArrayList<>();
                            while (elements.hasNext()) {
                                Map<String, String> headerMap = new HashMap<>();
                                JsonNode node = elements.next();
                                headerMap.put("key", node.get("key").asText());
                                headerMap.put("value", node.get("value").asText());
                                headerMapList.add(headerMap);
                            }
                            headersList.put(requestNode.get("method").asText(), headerMapList);
//                            System.out.println("HeadersList: " + headersList);
                        }
                    }
                }
                getMethodsAndHeadersForUrl(item, url, methods, headersList);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("methods", methods);
        response.put("headers", headersList);
        return response;
    }
    */
    
    public static JsonNode removeNodesWithDuplicateUrl(JsonNode jsonNode, JsonNode node, Set<String> urlsSet, ObjectMapper mapper) {
        ObjectNode objectNode = (ObjectNode) node;
//        Map<String, String> urlMethodMap = new HashMap<>();
//        if (objectNode.has("item")) {
            JsonNode itemNode = objectNode.get("item");
            if (itemNode != null && itemNode.isArray()) {
                for (JsonNode item : itemNode) {
                    JsonNode requestNode = item.get("request");
                    if (requestNode != null) {
                        JsonNode urlNode = requestNode.get("url");
                        if (urlNode != null) {
                            String url = urlNode.has("raw") ? urlNode.get("raw").asText() : urlNode.asText();
                            String path = url.split("\\?")[0];
                            
                            Map<String, Object> methodsAndHeadersForUrl = getMethodHeadersAndQueryForUrl(jsonNode, url, new ArrayList<>(), new HashMap<String, Object>(), new HashMap<String, List<String>>());
                            
                            List<String> methodsForUrl = mapper.convertValue(methodsAndHeadersForUrl.get("methods"), new TypeReference<List<String>>() {
                            });
                            JsonNode queryForUrl = mapper.convertValue(methodsAndHeadersForUrl.get("queryParams"), JsonNode.class);
                            JsonNode headersForUrl = mapper.convertValue(methodsAndHeadersForUrl.get("headers"), JsonNode.class);
//                        System.out.println(methodsForUrl);
                            if (!urlsSet.contains(path)) {
                                urlsSet.add(path);
                                if (methodsForUrl.size() > 1) {
                                    ArrayNode methodNode = mapper.createArrayNode();
                                    for (String method : methodsForUrl) {
                                        methodNode.add(method);
                                    }
                                    ArrayNode headerNode = mapper.createArrayNode();
                                    headerNode.add(headersForUrl);
                                    ((ObjectNode) requestNode).remove("method");
                                    ((ObjectNode) requestNode).set("method", methodNode);
                                    ((ObjectNode) requestNode).remove("header");
                                    ((ObjectNode) requestNode).set("header", headerNode);
                                    ((ObjectNode) requestNode).set("queryParams", queryForUrl);
                                }
                            } else {
                                System.out.println(url);
                                ((ObjectNode) requestNode).removeAll();
                            }

                        }
                    }
                    removeNodesWithDuplicateUrl(jsonNode, item, urlsSet, mapper);
                }
            }
//        }
//        System.out.println("MAP: " + urlMethodMap);
//        System.out.println(node.toPrettyString());
        return node;
    }
        
    private static Map<String, Object> getMethodHeadersAndQueryForUrl(JsonNode jsonNode, String url, ArrayList<String> methods, Map<String, Object> headersList, Map<String, List<String>> queryParams) {
        if (jsonNode.has("item")) {
            JsonNode itemNode = jsonNode.get("item");
            for (JsonNode item : itemNode) {
                JsonNode requestNode = item.get("request");
                if (requestNode != null && requestNode.get("url") != null) {
                    JsonNode urlNode = requestNode.get("url");
                    String currentUrl = urlNode.has("raw") ? urlNode.get("raw").asText() : urlNode.asText();
                    String currentPath = currentUrl.split("\\?")[0];
                    String path = url.split("\\?")[0];
                    String.format("CurrentPath URL: %s, Path Url: %s ", currentPath, path);
                    if (currentPath.equals(path) && !requestNode.get("method").asText().isBlank()
                        && !methods.contains(requestNode.get("method").asText())) {
                        String method = requestNode.get("method").asText();
                        methods.add(requestNode.get("method").asText());
                        
                        if (currentUrl.split("\\?").length > 1) {
                            String[] queryList = currentUrl.split("\\?")[1].split("\\&");
                            List<String> list = new ArrayList<>();
                            for (String query : queryList) {
                                list.add(query);
                            }
                            queryParams.put(requestNode.get("method").asText(), list);
                        }

                        if (requestNode.has("header") && !requestNode.get("header").isEmpty()  && !requestNode.get("method").asText().isEmpty()) {
                            JsonNode headerNode = requestNode.get("header");
                            Iterator<JsonNode> elements = headerNode.elements();
                            List<Map<String, String>> headerMapList = new ArrayList<>();
                            while (elements.hasNext()) {
                                Map<String, String> headerMap = new HashMap<>();
                                JsonNode node = elements.next();
                                headerMap.put("key", node.get("key").asText());
                                headerMap.put("value", node.get("value").asText());
                                headerMapList.add(headerMap);
                            }
                            headersList.put(requestNode.get("method").asText(), headerMapList);
//                            System.out.println("HeadersList: " + headersList);
                        }
                    }
                }
                getMethodHeadersAndQueryForUrl(item, url, methods, headersList, queryParams);
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("methods", methods);
        response.put("headers", headersList);
        response.put("queryParams", queryParams);
        return response;
    }

    
    public static List<Map<String, Object>> removeDuplicateUrlsStream(List<Map<String, Object>> json, Set<String> urlsSet,
        ObjectMapper mapper) {
        List<Map<String, Object>> result = json.stream().map(items -> {
            List<Map<String, Object>> item = (List<Map<String, Object>>) items.get("item");
//            String item = (String) items.get("name");
//            System.out.println("HERE!!!" + item);
            if (item != null && !item.isEmpty()) {
                item.stream().map(itm -> {
                    Map<String, Object> requestMap = (Map<String, Object>) itm.get("request");
                    if (requestMap != null) {
//                        Object object = requestMap.get("url");
//                        (requestMap.get("url") instanceof String) ? (String) requestMap.get("url") : (String) requestMap.get("url").get("raw");

                        String url = null;
                        if (requestMap.get("url") instanceof String) {
                            url = (String) requestMap.get("url");
                        } else {
                            Map<String, Object> mapUrl = (Map<String, Object>) requestMap.get("url");
                            url = (String) mapUrl.get("raw");
                        }
                        Map<String, Object> request = (Map<String, Object>) requestMap.get("");
                        GetMethodsForUrlStream(json, new ArrayList<>(), url);
                        return request;
                    }
                    return removeDuplicateUrlsStream(item, urlsSet, mapper);
                });
            }
            return items;
        }).collect(Collectors.toList());
        return result;
    }
    
    private static void GetMethodsForUrlStream(List<Map<String, Object>> json, List<String> methods, String url) {
        
    }

       
    private static JsonNode groupMethods(JsonNode collection) {
        Map<String, List<String>> urlMethodMap = new HashMap<>();

        // Iterate over the requests in the collection
        JsonNode itemNode = collection.get("item");
        if(itemNode.isArray()) {
        for (JsonNode requestNode : itemNode) {
//            JsonNode requestNode = item.path("request");
            if (requestNode != null) {
                String url = requestNode.path("url").path("raw").asText();
                String method = requestNode.path("method").asText();
                // Check if the URL already exists in the map
                if (urlMethodMap.containsKey(url)) {
                    // Add the method to the existing list of methods
                    urlMethodMap.get(url).add(method);
                } else {
                    // Create a new list and add the method
                    List<String> methods = new ArrayList<>();
                    methods.add(method);
                    urlMethodMap.put(url, methods);
                }
            }
        }
        }

        // Recreate the collection with unique URLs and associated methods
        List<Map<String, Object>> uniqueCollection = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : urlMethodMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("request", Map.of("url", Map.of("raw", entry.getKey())));
            item.put("methods", entry.getValue());
            uniqueCollection.add(item);
        }
        System.out.println("Coll: " + uniqueCollection);
        
        return collection;
    }
    
    private static JsonNode addNameIfMissing(JsonNode node) {
            ObjectNode objectNode = (ObjectNode) node;
            if (objectNode.has("item")) {
                JsonNode itemNode = objectNode.get("item");
                if (itemNode.isArray()) {
                    for (JsonNode item : itemNode) {
                        if (!item.has("name") || item.get("name").asText().isEmpty()) {
                            ((ObjectNode) item).put("name", "default"); // Set your default name here
                        }
                        addNameIfMissing(item);
                    }
                }
            }
            return node;
    }
        
    private static JsonNode addPathAndQueryIfMissing(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = (ObjectNode)jsonNode;
        if (objectNode.has("item")) {
            JsonNode itemNode = objectNode.get("item");
            for (JsonNode item : itemNode) {
                if(item.has("request")) {
//                    System.out.println(item.get("request").get("url"));
                    if (item.get("request").get("url") != null) {
                        JsonNode urlNode = item.get("request").get("url");
                        if (urlNode.get("raw") != null) {
                            String rawUrl = urlNode.get("raw").asText().replaceAll("^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/\\n]+)\\/", "/")
                                .replace("//", "/");
//                            System.out.println(rawUrl);
                            String path = rawUrl.split("\\?")[0];
                            String[] pathArray = path.split("/");

                            // Adding path
                            if (!urlNode.has("path") || urlNode.get("path").asText().isEmpty()) {
                                ArrayNode arrayNode = mapper.createArrayNode();
                                for (String pth : pathArray) {
                                    if (!pth.isEmpty()) {
                                        arrayNode.add(pth);
                                    }
                                }
                                ((ObjectNode) urlNode).set("path", arrayNode);
                            }
                            
                            // Adding query
                            if (rawUrl.split("\\?").length > 1) {
                                String query = rawUrl.split("\\?")[1];
                                String[] queryArray = query.split("\\&");
                                if (!urlNode.has("query") || urlNode.get("query").asText().isEmpty()) {
                                    ArrayNode arrayNode = mapper.createArrayNode();
                                    for (String qry : queryArray) {
//                                        System.out.println(qry);
                                        String[] subQuery = qry.split("=");
                                        ObjectNode mapNode = mapper.createObjectNode();
                                        mapNode.put("key", subQuery[0]);
                                        mapNode.put("value", subQuery[1]);
                                        arrayNode.add(mapNode);
                                    }
                                    ((ObjectNode) urlNode).set("query", arrayNode);
                                }
                            }
                        }
                    }
                }
                addPathAndQueryIfMissing(item);
            }
        }
        return jsonNode;
    }
    
    public static JsonNode swaggerApiPreprocess(Resource resource) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String inputPayload = readFromInputStream(resource.getInputStream());
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        JsonNode jsonNode = null;
        if (inputPayload.startsWith("{")) {
            jsonNode = objectMapper.readValue(inputPayload, JsonNode.class);
        } else {
            jsonNode = yamlMapper.readValue(inputPayload, JsonNode.class);
        }
        
        Set<String> tagsSet = new HashSet<>();
        List<Map<String, String>> tagsList = new ArrayList<>();

        if (jsonNode.has("tags")) {
            JsonNode tagNode = jsonNode.path("tags");
            Iterator<JsonNode> elements = tagNode.elements();
            while (elements.hasNext()) {
                JsonNode name = elements.next().get("name");
//                System.out.println(name);
                Map<String, String> tgMap = new HashMap<>();
                tgMap.put("name", name.asText());
                tagsList.add(tgMap);
                tagsSet.add(name.asText());
            }
        }

        JsonNode pathNode = jsonNode.path("paths");
        
        Iterator<Entry<String, JsonNode>> fields = pathNode.fields();
        while (fields.hasNext()) {
            Entry<String, JsonNode> next = fields.next();
            Iterator<JsonNode> elements = next.getValue().elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                if (!element.path("tags").isArray() || element.path("tags").size()==0) { 
                    ((ObjectNode) element).putArray("tags").add("default");
                }
                JsonNode tagsNode = element.path("tags");
                if (tagsNode.isArray()) {
                    for (JsonNode tag : tagsNode) {
                        if (!tagsSet.contains(tag.asText())) {
                            Map<String, String> tagsMap = new HashMap<>();
                            tagsSet.add(tag.asText());
                            tagsMap.put("name", tag.asText());
                            tagsList.add(tagsMap);
                        }
                    }
                }
            }
        }
        JsonNode listNode = objectMapper.convertValue(tagsList, JsonNode.class);
        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.put("tags", listNode);

//        System.out.println(objectNode.toPrettyString());
        
        return objectNode;
    }

    public static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /*
     * "tags": [ { "name": "pet", "description": "Everything about your Pets", "externalDocs": { "description": "Find out more", "url":
     * "http://swagger.io" } }, { "name": "store", "description": "Access to Petstore orders" }, { "name": "user", "description":
     * "Operations about user", "externalDocs": { "description": "Find out more about our store", "url": "http://swagger.io" } } ],
     * 
     */

}
