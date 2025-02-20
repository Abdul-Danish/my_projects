package com.minio.dto;

import java.util.List;

import lombok.Data;

@Data
public class MultiPartRequest {

    private String uploadId;
    // path of local file
    private String path;
    // file path for minio
    private String minioFilePath;
    private int partSize;
    private List<MultiPart> parts;
    
}
