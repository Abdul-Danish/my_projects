package com.minio.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minio.dto.MinioRequestDto;
import com.minio.service.MinioService;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

@RestController
@RequestMapping("/api/v1/minio")
public class MinioController {

    @Autowired
    private MinioService minioService;

    @GetMapping("/buckets/list")
    public ResponseEntity<List<String>> getBucketList() {
        return ResponseEntity.ok(minioService.getBucketList());
    }

    @PostMapping("/upload/object")
    public void uploadObject(@RequestPart MultipartFile file, @RequestParam String filePath) throws IOException, MinioException {
        minioService.uploadObject(file.getOriginalFilename(), file.getBytes(), filePath);
    }

    @GetMapping("/file")
    public ResponseEntity<String> getFile(@RequestBody MinioRequestDto minioRequestDto) {
        return ResponseEntity.ok(minioService.getFile(minioRequestDto));
    }

    @GetMapping("/presigned/url")
    public ResponseEntity<String> getPresignedUrl(@RequestBody MinioRequestDto minioRequestDto) {
        return ResponseEntity.ok(minioService.getPresignedUrl(minioRequestDto));
    }

    @DeleteMapping("/remove")
    public void removeObject(@RequestBody MinioRequestDto minioRequestDto)
        throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException,
        NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
        minioService.removeObject(minioRequestDto);
    }

}
