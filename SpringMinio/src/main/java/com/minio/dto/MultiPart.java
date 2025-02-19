package com.minio.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPart {

    private int partNo;
    private String url;
    private String etag;    
}
