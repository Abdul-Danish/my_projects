package com.minio.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Multimap;

import io.minio.CreateMultipartUploadResponse;
import io.minio.MinioAsyncClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadPartResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Part;

@Component
public class MinioClientWrapper extends MinioAsyncClient {

    @Value("${minio.bucket.name}")
    private String bucketName;

    protected MinioClientWrapper(MinioAsyncClient client) {
        super(client);
    }

    public String initMultiPartUpload(String region, String objectName, Multimap<String, String> headers,
        Multimap<String, String> extraQueryParams) throws InvalidKeyException, InsufficientDataException, InternalException,
        NoSuchAlgorithmException, XmlParserException, IOException, InterruptedException, ExecutionException {
        CompletableFuture<CreateMultipartUploadResponse> response = this.createMultipartUploadAsync(bucketName, region, objectName, headers,
            extraQueryParams);
        return response.get().result().uploadId();
    }

    public ObjectWriteResponse completeMultiPartUpload(String region, String objectName, String uploadId, Part[] parts,
        Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams)
        throws InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, ServerException, XmlParserException,
        ErrorResponseException, InternalException, InvalidResponseException, IOException, InterruptedException, ExecutionException {
        CompletableFuture<ObjectWriteResponse> response = this.completeMultipartUploadAsync(bucketName, region, objectName, uploadId, parts,
            extraHeaders, extraQueryParams);
        return response.get();
    }

    public String createMultiPartUpload(String objectNmae) throws InvalidKeyException, InsufficientDataException, InternalException,
        NoSuchAlgorithmException, XmlParserException, IOException, InterruptedException, ExecutionException {
        CompletableFuture<CreateMultipartUploadResponse> multipartUploadResponse = this.createMultipartUploadAsync(bucketName, null,
            objectNmae, null, null);
        return multipartUploadResponse.get().result().uploadId();
    }

    public String uploadPart(String objectName, InputStream partStream, int length, String uploadId, int partNumber)
        throws InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, ServerException, XmlParserException,
        ErrorResponseException, InternalException, InvalidResponseException, IOException {
        UploadPartResponse uploadPartResponse = this.uploadPart(bucketName, null, objectName, partStream, length, uploadId, partNumber,
            null, null);
        return uploadPartResponse.etag();
    }

}
