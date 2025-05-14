package com.rest.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.rest.model.Feedback;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class MultipartController {

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(MultipartHttpServletRequest request,
        @RequestPart(required = false, value = "afiles") List<MultipartFile> multipartFiles,
        @RequestPart(required = false, value = "afile") MultipartFile multipartFile,
        @RequestParam(required = false) Map<String, Object> payload
//        @RequestParam Map<String, Object> payload
    ) throws IOException {
        log.info("File Received");

        log.info("param: {}", request.getParameterMap());
        log.info("file map: {}", request.getFileMap());

        log.info("files; {}", multipartFiles);
        log.info("file; {}", multipartFile);
        log.info("payload; {}", payload);

        if (Objects.nonNull(multipartFiles)) {
            multipartFiles.forEach(file -> {
                System.out.println("files name: " + file.getOriginalFilename());
            });
        }

//        for (MultipartFile file : multipartFiles) {
        log.info("File Name: {}", multipartFile.getName());
        log.info("Original File Name: {}", multipartFile.getOriginalFilename());
        log.info("Content Type: {}", multipartFile.getContentType());
//        }
        log.info("Payload: {}", payload);
        log.info("Key {}", (payload != null ? payload.get("key1") : null));

        FileOutputStream fileOS = null;
        InputStream is = null;
        try {
            File localFile = new File("/home/danish/Documents/test-" + UUID.randomUUID().toString() + ".pdf");
            fileOS = new FileOutputStream(localFile);
            if (!localFile.isFile()) {
                log.info("Creating File");
                localFile.createNewFile();
            }
            is = multipartFile.getInputStream();
            fileOS.write(is.readAllBytes());
        } catch (Exception e) {
            log.error("Exception while Writing File");
        } finally {
            if (Objects.nonNull(fileOS)) {
                fileOS.close();
            }
            if (Objects.nonNull(is)) {
                is.close();
            }
        }
        
        FileOutputStream fileOSMP = null;
        InputStream isMP = null;
        try {
            if (Objects.nonNull(multipartFiles)) {
                for (MultipartFile file : multipartFiles) {
                    File localFileMP = new File("/home/danish/Documents/test-" + UUID.randomUUID().toString() + ".pdf");
                    fileOSMP = new FileOutputStream(localFileMP);
                    if (!localFileMP.isFile()) {
                        log.info("Creating File");
                        localFileMP.createNewFile();
                    }
                    isMP = file.getInputStream();
                    fileOSMP.write(isMP.readAllBytes());
                }
            } 
        } catch (Exception e) {
            log.error("Exception while Writing Files");
        } finally {
            if (Objects.nonNull(fileOSMP)) {
                fileOSMP.close();
            }
            if (Objects.nonNull(isMP)) {
                isMP.close();
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", "Done");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/upload/url/encoded", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<Map<String, Object>> uploadUrlEncoded(Feedback feedback) {
        log.info("Feedback: {}", feedback.toString());
        Map<String, Object> response = new HashMap<>();
        response.put("data", "Feedback Received");
        return ResponseEntity.ok(response);
    }

}
