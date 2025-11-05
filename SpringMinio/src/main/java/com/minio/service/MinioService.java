package com.minio.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.minio.config.MinioClientWrapper;
import com.minio.dto.MinioRequestDto;
import com.minio.dto.MultiPart;
import com.minio.dto.MultiPartPresignedResponse;
import com.minio.dto.MultiPartRequest;

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
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioClientWrapper minioClientWrapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${minio.bucket.name}")
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

    public String getUploadPresignedUrl(String filePath) {
        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(filePath)
            .method(Method.PUT).build();
        try {
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (Exception e) {
            throw new RuntimeException("Exception Occured While Getting Pre-Signed Url for the Object: ", e);
        }
    }

    public void multiPartFileUploadAlpha(InputStream content, String fileName, int partSizeMb)
        throws InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, ServerException, XmlParserException,
        ErrorResponseException, InternalException, InvalidResponseException, IOException, InterruptedException, ExecutionException {
        int partSizeBytes = partSizeMb * 1024 * 1024;
        log.info("PartSize: {}", partSizeBytes);
        log.info("Content Length: {}", content.available());

        int partArraySize = (int) Math.ceil((double) content.available() / partSizeBytes);
        log.info("array size: {}", partArraySize);

        // Initiating Multi Part Upload
        String uploadId = minioClientWrapper.createMultiPartUpload(fileName);
        log.info("UploadId: {}", uploadId);

        Part[] parts = new Part[partArraySize + 1];
        byte[] buffer = new byte[partSizeBytes];
        int readBytes;
        int partNumber = 1;

        /*
         * "content.read(buffer)": reads the bytes from content into buffer
         * "readBytes": total number of bytes read into the buffer
         */
        while ((readBytes = content.read(buffer)) != -1) {
            log.info("Read Bytes: {}, Part Number: {}", readBytes, partNumber);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer, 0, readBytes);

            String etag = minioClientWrapper.uploadPart(fileName, inputStream, readBytes, uploadId, partNumber);
            log.info("eTag: {}", etag);

            // upload each part
            parts[partNumber] = new Part(partNumber, etag);
            partNumber++;
        }

        // complete upload
        minioClientWrapper.completeMultiPartUpload(null, fileName, uploadId, parts, null, null);
        log.info("Multi Part Upload Completed");
    }

    public MultiPartPresignedResponse getMultiPartPresignedUrlAlpha(String objectName, int noOfParts)
        throws InvalidKeyException, InsufficientDataException, InternalException, NoSuchAlgorithmException, XmlParserException, IOException,
        InterruptedException, ExecutionException, ErrorResponseException, InvalidResponseException, ServerException {
        if (noOfParts < 1) {
            return null;
        }
        String uploadId = minioClientWrapper.initMultiPartUpload(null, objectName, null, null);

        List<MultiPart> multiParts = new ArrayList<>();
        Map<String, String> queryParams = new HashMap<>();
        for (int partNo = 0; partNo < noOfParts; partNo++) {
            queryParams.put("uploadId", uploadId);
            queryParams.put("partNumber", String.valueOf(partNo + 1));

            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder().method(Method.PUT).bucket(bucketName)
                .object(objectName).extraQueryParams(queryParams).build();
            String presignedObjectUrl = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
            multiParts.add(MultiPart.builder().partNo(partNo + 1).url(presignedObjectUrl).build());
        }

        log.info("Multi-Part Presigned Url Generated");
        return MultiPartPresignedResponse.builder().filePath(objectName).multiParts(multiParts).uploadId(uploadId).build();
    }

    public void multiPartFileUpload(MultiPartRequest multiPartFile) throws IOException {
        File file = new File(multiPartFile.getPath());
        byte[] content = Files.readAllBytes(file.toPath());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        log.info("Content Size: {}", inputStream.available());
        log.info("multi part request: {}", multiPartFile);
        int partSizeBytes = inputStream.available() / multiPartFile.getPartSize();
        try {
            List<MultiPart> completedmultiParts = new ArrayList<>();
            for (MultiPart multiPart : multiPartFile.getParts()) {
                new Part(multiPart.getPartNo(), multiPart.getEtag());

                int bytesToRead = Math.min(partSizeBytes, inputStream.available());
                log.info("available input stream: {}", inputStream.available());
                byte[] readBytes = new byte[bytesToRead];
                int actuallyRead = inputStream.read(readBytes, 0, bytesToRead); // inputStream.read(): moves the content to the buffer
                log.info("Actually Read: {}", actuallyRead);

                log.info("Presigned URL: {}", multiPart.getUrl());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                HttpEntity<byte[]> httpEntity = new HttpEntity<>(readBytes, headers);

                ResponseEntity<String> response = restTemplate.exchange(URI.create(multiPart.getUrl()), HttpMethod.PUT, httpEntity,
                    String.class);
                String etag = response.getHeaders().getFirst("ETag");
                log.info("ETag: {}", etag);

                completedmultiParts.add(MultiPart.builder().partNo(multiPart.getPartNo()).url(multiPart.getUrl()).etag(etag).build());
            }
            log.info("Multi Parts: {}", completedmultiParts);
            completeMultiPartUpload(multiPartFile.getMinioFilePath(), multiPartFile.getUploadId(), completedmultiParts,
                multiPartFile.getPartSize());
        } catch (Exception e) {
            throw new RuntimeException("Exception: ", e);
        } finally {
        }
    }

    private void completeMultiPartUpload(String filePath, String uploadId, List<MultiPart> multiParts, int partSize)
        throws InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, ServerException, XmlParserException,
        ErrorResponseException, InternalException, InvalidResponseException, IOException, InterruptedException, ExecutionException {
        Part[] parts = new Part[partSize];
        int idx = 0;
        for (MultiPart part : multiParts) {
            parts[idx] = new Part(part.getPartNo(), part.getEtag());
            idx++;
        }

        minioClientWrapper.completeMultiPartUpload(null, filePath, uploadId, parts, null, null);
        log.info("Upload Completed");
    }

}
