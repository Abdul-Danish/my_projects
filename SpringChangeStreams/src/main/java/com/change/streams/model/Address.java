package com.change.streams.model;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {

    @Id
    private String id;
    private String zipCode;
}
