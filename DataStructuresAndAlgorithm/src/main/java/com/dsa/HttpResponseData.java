package com.dsa;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpResponseData {
    
    private int statusCode;
    private String responseBody;
}
