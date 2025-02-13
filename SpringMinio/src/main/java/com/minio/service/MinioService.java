package com.minio.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.minio.dto.MinioRequestDto;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.buckek.name}")
    private String bucketName;

    public List<String> getBucketList() {
        try {
            List<String> bucketsName = new ArrayList<>();
            List<Bucket> listBuckets = minioClient.listBuckets();
            for (Bucket bucket : listBuckets) {
                bucketsName.add(bucket.name());
            }
            return bucketsName;
        } catch (Exception e) {
            log.error("Exception Occured While get Bucket List ", e);
            throw new RuntimeException(e);
        }
    }

    public void uploadObject(String fileName, byte[] content, String filePath) throws IOException, MinioException {
        String contentType = null;
        try {
            log.info("File Name: {}", fileName);
            String fileExtension = fileName.split("\\.")[1];
            if ("pdf".toString().equals(fileExtension)) {
                contentType = MediaType.APPLICATION_PDF.toString();
            } else {
                contentType = MediaType.APPLICATION_JSON.toString();
            }
            log.info("content type: {}", contentType);

            filePath = !filePath.endsWith(File.separator) ? filePath + File.separator : filePath;
            filePath = filePath.concat(fileName);
            log.info("Constructed File Path: {}", filePath);
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(filePath).contentType(contentType)
                .stream(new ByteArrayInputStream(content), content.length - 1, -1).build();
            ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
            log.info("Upload Response: {}", response);
        } catch (Exception e) {
            log.error("Exception While Uploading Object: ", e);
            throw new MinioException("Failed to Upload Object: ");
        }
    }

    public String getFile(MinioRequestDto minioRequestDto) {
        log.info("File Path is: {}", minioRequestDto.getFilePath());
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(minioRequestDto.getFilePath()).build();
        try {
            GetObjectResponse object = minioClient.getObject(getObjectArgs);
            log.info("Object Retrieved: {}", object.object());
            byte[] objectBytes = object.readAllBytes();
            return Base64.getEncoder().encodeToString(objectBytes);
        } catch (Exception e) {
            throw new RuntimeException("Exception Occured While Getting File: ", e);
        }
    }

    public void removeObject(MinioRequestDto minioRequestDto) throws InvalidKeyException, ErrorResponseException, InsufficientDataException,
        InternalException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName).object(minioRequestDto.getFilePath()).build();
        minioClient.removeObject(removeObjectArgs);
        log.info("Object Removed: {}", minioRequestDto.getFilePath());
    }

    public String getPresignedUrl(MinioRequestDto minioRequestDto) {
        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder().bucket(bucketName)
            .object(minioRequestDto.getFilePath()).method(Method.GET).build();
        try {
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (Exception e) {
            throw new RuntimeException("Exception Occured While Getting Pre-Signed Url for the Object: ", e);
        }
    }

}
