package com.xslt.processor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JsonProcessor {
    
//    public static void main(String args[]) throws IOException, TransformerException {
//        String process = process();
//        System.out.println(process);
//    }

//    @Autowired
//    private YamlToJsonConverter yamlToJsonConverter;

    @Autowired
    private ResourceLoader resourceLoader;
    
    @Autowired
    private Run jsonProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    public String process() throws IOException, TransformerException {        
        StreamResult result = new StreamResult(new ByteArrayOutputStream());
        try { 
             /* 
              * Swagger inputs and transformer
              */
//            Resource resource = resourceLoader.getResource("classpath:/json_files/" + "ghes-3.11.2022-11-28_Mod.json");
//            Resource resource = resourceLoader.getResource("classpath:/json_files/" + "pet_store_swagger.json");
//             this.getClass().getResourceAsStream("classpath:/yaml_files/" + "pet_store_swagger.yaml");
//            Resource resource = resourceLoader.getResource("classpath:/json_files/" + "pet_store_swagger.json");
//            Resource resource = resourceLoader.getResource("classpath:/practice/" + "practice2.json");
//            JsonNode convertedJson = Run.swaggerApiPreprocess(resource);

//            Source xsltSource = new StreamSource(resourceLoader.getResource("classpath:/templates/" + "swaggerApisTransformer_version_5.0.xsl").getInputStream());
//            Source xsltSource = new StreamSource(resourceLoader.getResource("classpath:/templates/" + "jsonToXml.xsl").getInputStream());
//            Source xsltSource = new StreamSource(resourceLoader.getResource("classpath:/practice/" + "grouping_test.xsl").getInputStream());
            
//            Resource resource = resourceLoader.getResource("classpath:/json_files/" + "");
//            Source xsltSource = new StreamSource(resourceLoader.getResource("classpath:/templates/" + "").getInputStream());
            
            
            /*
             * Postman inputs and transformer
             */
            Resource resource = resourceLoader.getResource("classpath:/postman/platform_jsons/" + "DD-Foundation.postman_collection.json");
//          Resource resource = resourceLoader.getResource("classpath:/postman/jsons/" + "petstore_postman_collection_Mod.json");
//          Resource resource = resourceLoader.getResource("classpath:/postman/jsons/" + "Level3_Sample Collection.postman_collection.json");
//          Resource resource = resourceLoader.getResource("classpath:/postman/jsons/" + "petstore_postman_collection_Mod.json");
            
          JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());
          JsonNode convertedJson = Run.removeNodesWithDuplicateUrl(jsonNode, jsonNode, new HashSet<>(), objectMapper);
//          System.out.println(convertedJson.toPrettyString());
          
          Source xsltSource = new StreamSource(resourceLoader.getResource("classpath:/postman/transformer/" + "postmanApisTransformer_version_9.0.xsl").getInputStream());
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            transformer.setParameter("input", convertedJson.toString());
//            transformer.setParameter("input", readFromInputStream(resource.getInputStream()));
            transformer.transform(null, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String output = result.getOutputStream().toString();
        
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> tags = objectMapper.readValue(output,
            new TypeReference<List<Map<String, Object>>>() {
            });

        List<Map<String, Object>> modifiedTags = tags.stream().map(tag -> {
            List<Map<String, Object>> paths = (List<Map<String, Object>>) tag.get("paths");
            if (paths != null) {
                List<Map<String, Object>> modifiedPaths = paths.stream().map(path -> {
                    String value = (String) path.get("value");
                    String updatedValue = value
                        .replaceAll("^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/\\n]+)\\/", "/").replace("//", "/");
                    path.put("value", updatedValue);
                    return path;
                }).collect(Collectors.toList());
                tag.put("paths", modifiedPaths);
            }
            return tag;
        }).collect(Collectors.toList());
        
        JsonNode res = objectMapper.convertValue(modifiedTags, JsonNode.class);
        System.out.println(res.toPrettyString());
        
        return output;

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
    if (resource.getFilename().endsWith("yaml")) {
        log.info("IF BLOCK");
        String convertedJson = yamlToJsonConverter.convert(resource);

        Source xsltSource = new StreamSource(
            resourceLoader.getResource("classpath:/templates/" + "swaggerApisTransformer_version_2.0.xsl").getInputStream());
        // Source xsltSource = new StreamSource(resourceLoader.getResource("classpath:/templates/" +
        // "jsonToXml.xsl").getInputStream());
        // Source xsltSource = new StreamSource(resourceLoader.getResource("classpath:/practice/" +
        // "grouping_test.xsl").getInputStream());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(xsltSource);

        transformer.setParameter("input", convertedJson);

        transformer.transform(null, result);
    } else {
    */
    
}
