package com.minio.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPartPresignedResponse {

    private String filePath;
    private List<MultiPart> multiParts;
    private String uploadId;
}
