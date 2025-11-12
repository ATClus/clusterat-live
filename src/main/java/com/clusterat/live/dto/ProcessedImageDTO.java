package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedImageDTO {
    @JsonProperty("image_id")
    private String imageId;

    @JsonProperty("image_path")
    private String imagePath;

    @JsonProperty("dpi")
    private Integer dpi;

    @JsonProperty("format")
    private String format;

    @JsonProperty("size_kb")
    private Double sizeKb;
}

