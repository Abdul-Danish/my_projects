package com.psql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TemplateFile {

    private String file_path;
    private String image_path;
    private String filename;
    private String file_extension;
    private long file_size;
    
}
