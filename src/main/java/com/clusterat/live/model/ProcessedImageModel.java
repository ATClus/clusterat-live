package com.clusterat.live.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedImageModel {
    private String imageId;
    private String imagePath;
    private Integer dpi;
    private String format;
    private Double sizeKb;
}

