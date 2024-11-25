package com.xslt;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xslt.processor.JsonProcessor;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class XsltApplication {

	public static void main(String[] args) throws IOException, TransformerException {
		ConfigurableApplicationContext context = SpringApplication.run(XsltApplication.class, args);
		
		JsonProcessor bean = context.getBean(JsonProcessor.class);
		String process = bean.process();
		
		System.out.println("Res " + process);
	}

	
//	@Bean
//	public ObjectMapper objectMapperBean(ObjectMapper objectMapper) {
//	    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//	    return objectMapper;
//	}
}
