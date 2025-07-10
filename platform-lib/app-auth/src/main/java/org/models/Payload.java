package org.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload implements Serializable {

    private static final long serialVersionUID = 1925185005259523302L;
    private String data;
}

