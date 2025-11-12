package com.clusterat.live.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ocr_pre_processing", schema = "live")
public class OcrPreProcessingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "extracted_text", columnDefinition = "TEXT")
    private String extractedText;

    @Column(name = "image_count")
    private Integer imageCount;

    @Column(name = "processing_status")
    private String processingStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}

