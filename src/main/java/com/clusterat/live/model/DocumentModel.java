package com.clusterat.live.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentModel {
    private String id;
    private String originalFilename;
    private String filePath;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProcessedImageModel> processedImages;
    private String errorMessage;
}

