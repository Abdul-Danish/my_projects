package com.minio.dto;

import java.util.List;

import lombok.Data;

@Data
public class MultiPartRequest {

    private String uploadId;
    private String filePath;
    private List<MultiPart> parts;
    
}
