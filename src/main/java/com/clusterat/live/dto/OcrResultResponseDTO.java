package com.clusterat.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultResponseDTO {
    private String documentId;
    private String documentName;
    private String extractedText;
    private Integer imageCount;
    private String processingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;
}

