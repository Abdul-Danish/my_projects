package com.ibmmq.receiver.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JmsFilter {

    private String key;
    private String value;
    private JmsOperator operator;
    
}
