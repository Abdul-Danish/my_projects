package com.dsa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.InputStreamBody;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class DataStructuresAndAlgorithmApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DataStructuresAndAlgorithmApplication.class, args);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

//	@RestController
//	class TestController {
//	    
//	    public void sendRequest() {
//	        
//	    }
//	    
//	}

    @Bean
    RestTemplate restTemplateBean() {
        return new RestTemplate();
    }

    @Bean
    ObjectMapper objectMapperBean() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void run(String... args) throws Exception {
//        FileInputStream pamelaIS = new FileInputStream("/home/danish/Downloads/pamela.pdf");
//        FileInputStream adverseEventIS = new FileInputStream("/home/danish/Downloads/AdverseEvent_4.pdf");
//        new BufferedReader(new FileInputStream(pamelaIS));
//        pamelaIS.readAllBytes();

        //
        String encodedFilePM = "";
        byte[] decodedFilePM = Base64.getDecoder().decode(encodedFilePM);
        //
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        File pamelaFile = new File("/home/danish/Downloads/pamela.pdf");
        File adverseEventFile = new File("/home/danish/Downloads/AdverseEvent_4.pdf");
        ContentType contentTypePDF = ContentType.create("application/pdf");
//        entityBuilder.addPart("files", new FileBody(pamelaFile, contentTypePDF, "pamela.pdf"));
//        entityBuilder.addPart("files", new FileBody(adverseEventFile, contentTypePDF, "adverseEvent.pdf"));
//        entityBuilder.addPart("files", new InputStreamBody(new FileInputStream(adverseEventFile), contentTypePDF, "adverseEvent.pdf"));
//        entityBuilder.addPart("files", new InputStreamBody(new ByteArrayInputStream(decodedFilePM), contentTypePDF, "pamela.pdf"));
        //
        URL url = new URL("http://localhost:9001/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL3NwcmluZ21pbmlvL2RhbmlzaC9maWxlL3BhbWVsYS5wZGY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD0wWktSODJaMDRTUkMwNzlMMzFPNCUyRjIwMjUwMzIxJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MDMyMVQwNjUxMDdaJlgtQW16LUV4cGlyZXM9NDMxOTkmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUl3V2t0U09ESmFNRFJUVWtNd056bE1NekZQTkNJc0ltVjRjQ0k2TVRjME1qVTRNekF6TlN3aWNHRnlaVzUwSWpvaWJXbHVhVzloWkcxcGJpSjkuVjhoLVpfb2Z1NFlSbnNrSWpoRVI1Q2RyUjlGZk9hbVg2TUVCTFZDT05YaEhYdWtDUGxWcmFuNTlzTmJsOFF5R3NhNUMtdndSejBKQmg5d3V4YmwwaUEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT0wMWUyOWNlZTNiMjFlNzhlZDVjYzBmMDdhNTc2NzlkZGM1OWJjOGMyNDE2NWZhNjZhNTRkZGJhZGU0NmMzODc5".toString());
        entityBuilder.addPart("file", new InputStreamBody(url.openStream(), contentTypePDF, "pamela.pdf"));
        //
        Map<String, String> payload = new HashMap<>();
        payload.put("key1", "val1");
        entityBuilder.addPart("payload", new StringBody(objectMapper.writeValueAsString(payload), ContentType.APPLICATION_JSON));
//        entityBuilder.addTextBody("payload", objectMapper.writeValueAsString(payload), ContentType.APPLICATION_JSON);
        
        HttpEntity httpEntity = entityBuilder.build();

        log.info("httpEntity: {}", httpEntity.toString());
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://localhost:8000/api/v1/upload");
        post.setEntity(httpEntity);
        HttpResponseData res = httpClient.execute(post, new CustomHttpClientResponseHandler());
        log.info("Resquest Sent, Res: {}", res.getStatusCode());
        log.info("Res Body: {}", res.getResponseBody());
//        log.info("Res Body: {}", new String(res.getEntity().getContent().readAllBytes()));
        
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(res.getEntity().getContent().readAllBytes());
//        new String(decodedFilePM);
        
        httpClient.close();
        
        // Not Working
//
//        FileInputStream fileInputStream = null;
//        try {
//            fileInputStream = new FileInputStream(pamelaFile);
//            ByteArrayResource byteArrayResource = new ByteArrayResource(fileInputStream.readAllBytes());
//
//            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
//            multiValueMap.add("file", byteArrayResource);
////            multiValueMap.add("files", new InputStreamBody(new FileInputStream(pamelaFile), contentTypePDF, "pamela.pdf"));
////            multiValueMap.add("files", new InputStreamBody(new FileInputStream(adverseEventFile), contentTypePDF, "adverseEvent.pdf"));
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//            org.springframework.http.HttpEntity<MultiValueMap<String, Object>> reqEntity = new org.springframework.http.HttpEntity<>(
//                multiValueMap, headers);
//            ResponseEntity<String> res = restTemplate.exchange("http://localhost:8000/api/v1/upload", HttpMethod.POST, reqEntity,
//                String.class);
//            log.info("Resquest Sent, Res: {}", res.getStatusCode());
//        } catch (Exception e) {
//            log.error("Exception: ", e);
//        } finally {
//            fileInputStream.close();
//        }
    }

}
