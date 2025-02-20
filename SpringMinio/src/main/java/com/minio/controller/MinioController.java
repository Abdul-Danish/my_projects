package com.minio.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import com.minio.dto.MultiPartPresignedResponse;
import com.minio.dto.MultiPartRequest;
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

    @PostMapping("/upload")
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
    
    @GetMapping("/upload/presigned/url")
    public ResponseEntity<String> getUploadPresignedUrl(@RequestBody MinioRequestDto minioRequestDto) {
        return ResponseEntity.ok(minioService.getUploadPresignedUrl(minioRequestDto.getFilePath()));
    }

    @DeleteMapping("/remove")
    public void removeObject(@RequestBody MinioRequestDto minioRequestDto)
        throws InvalidKeyException, ErrorResponseException, InsufficientDataException, InternalException, InvalidResponseException,
        NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
        minioService.removeObject(minioRequestDto);
    }

    @PostMapping("/multipart/upload/alpha")
    public void uploadObjectAlpha(@RequestPart MultipartFile file, @RequestParam String fileName, @RequestParam int fileSize)
        throws InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, ServerException, XmlParserException,
        ErrorResponseException, InternalException, InvalidResponseException, IOException, InterruptedException, ExecutionException {
        minioService.multiPartFileUploadAlpha(file.getInputStream(), fileName, fileSize);
    }

    @GetMapping("/multipart/presigned/url/alpha")
    public ResponseEntity<MultiPartPresignedResponse> getMultiPartPresignedUrl(@RequestBody MinioRequestDto minioRequestDto)
        throws InvalidKeyException, InsufficientDataException, InternalException, NoSuchAlgorithmException, XmlParserException,
        ErrorResponseException, InvalidResponseException, ServerException, IOException, InterruptedException, ExecutionException {
        return ResponseEntity.ok(minioService.getMultiPartPresignedUrlAlpha(minioRequestDto.getFilePath(), minioRequestDto.getNoOfParts()));
    }

    @PostMapping("/multipart/upload")
    public void uploadObject(@RequestBody MultiPartRequest multiPartRequest)
        throws InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, ServerException, XmlParserException,
        ErrorResponseException, InternalException, InvalidResponseException, IOException, InterruptedException, ExecutionException {
        minioService.multiPartFileUpload(multiPartRequest);
    }

}
