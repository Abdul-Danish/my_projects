package com.xslt.processor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class YamlToJsonConverter {

//    @Autowired
//    private ResourceLoader resourceLoader;
    
//    @Autowired
//    private ObjectMapper objectMapper;

    public String convert(Resource resource) throws JsonMappingException, JsonProcessingException, IOException {

//        Resource resource = resourceLoader.getResource("classpath:/yaml_files/" + "pet_store_swagger.yaml");
        
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
//        String yamlString = resource.getContentAsString(Charset.defaultCharset());
        String yamlString = new String(resource.getInputStream().readAllBytes());
        Object yamlObject = yamlMapper.readValue(yamlString, Object.class);
        
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        String jsonOutput = jsonMapper.writeValueAsString(yamlObject);
//        log.info("Out {}", jsonOutput);
        return jsonOutput;
    }

}
