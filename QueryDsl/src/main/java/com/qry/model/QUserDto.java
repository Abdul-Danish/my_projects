package com.qry.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QUserDto {
    
    private String bid;
    private boolean active;

}
