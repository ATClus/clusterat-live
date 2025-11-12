package com.clusterat.live.controller;

import com.clusterat.live.dto.DocumentProcessResponseDTO;
import com.clusterat.live.dto.DocumentStatusResponseDTO;
import com.clusterat.live.dto.HealthCheckResponseDTO;
import com.clusterat.live.dto.OcrResultResponseDTO;
import com.clusterat.live.model.DocumentModel;
import com.clusterat.live.model.OcrPreProcessingModel;
import com.clusterat.live.model.ProcessedImageModel;
import com.clusterat.live.service.DocProcessorService;
import com.clusterat.live.service.FilesService;
import com.clusterat.live.service.OcrPreProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentsController {
    private final DocProcessorService docProcessorService;
    private final FilesService filesService;
    private final OcrPreProcessingService ocrPreProcessingService;

    @Autowired
    public DocumentsController(DocProcessorService docProcessorService, FilesService filesService, OcrPreProcessingService ocrPreProcessingService) {
        this.docProcessorService = docProcessorService;
        this.filesService = filesService;
        this.ocrPreProcessingService = ocrPreProcessingService;
    }

    @PostMapping("/process")
    public Mono<ResponseEntity<DocumentProcessResponseDTO>> processDocument(@RequestPart("file") Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(filePart -> filesService.store(filePart)
                        .then(Mono.fromCallable(() -> {
                                    Path uploadedFilePath = Paths.get("uploads").resolve(filePart.filename());
                                    DocumentModel documentModel = docProcessorService.processDocument(filePart.filename(), uploadedFilePath);
                                    return DocumentProcessResponseDTO.builder()
                                            .documentId(documentModel.getId())
                                            .status(documentModel.getStatus())
                                            .message("Document sent for processing")
                                            .build();
                                }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                                .map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response))))
                .onErrorResume(e -> {
                    log.error("Error processing file: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(DocumentProcessResponseDTO.builder()
                                    .status("failed")
                                    .message("Error processing file: " + e.getMessage())
                                    .build()));
                });
    }

    @GetMapping("/{document_id}/status")
    public ResponseEntity<DocumentStatusResponseDTO> getDocumentStatus(@PathVariable("document_id") String documentId) {

        DocumentModel documentModel = docProcessorService.getDocumentStatus(documentId);

        if (documentModel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DocumentStatusResponseDTO.builder()
                            .documentId(documentId)
                            .status("not_found")
                            .message("Document not found")
                            .build());
        }

        int progress = calculateProgress(documentModel);

        java.util.List<String> imageIds = documentModel.getProcessedImages() != null
                ? documentModel.getProcessedImages().stream()
                    .map(ProcessedImageModel::getImageId)
                    .toList()
                : null;

        DocumentStatusResponseDTO response = DocumentStatusResponseDTO.builder()
                .documentId(documentModel.getId())
                .status(documentModel.getStatus())
                .message(getStatusMessage(documentModel.getStatus()))
                .progress(progress)
                .processedImages(imageIds)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{document_id}/download/{image_id}")
    public ResponseEntity<?> downloadImage(@PathVariable("document_id") String documentId, @PathVariable("image_id") String imageId) {

        try {
            byte[] imageData = docProcessorService.downloadImage(documentId, imageId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageId + "\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageData);

        } catch (RuntimeException e) {
            log.error("Error downloading image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Error processing download: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error processing download\"}");
        }
    }

    @GetMapping("/{document_id}/ocr")
    public ResponseEntity<OcrResultResponseDTO> getOcrResult(@PathVariable("document_id") String documentId) {
        Optional<OcrPreProcessingModel> ocrResult = ocrPreProcessingService.getOcrResult(documentId);

        if (ocrResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcrResultResponseDTO.builder()
                            .documentId(documentId)
                            .processingStatus("not_found")
                            .errorMessage("OCR result not found for this document")
                            .build());
        }

        OcrPreProcessingModel ocr = ocrResult.get();
        OcrResultResponseDTO response = OcrResultResponseDTO.builder()
                .documentId(ocr.getDocumentId())
                .documentName(ocr.getDocumentName())
                .extractedText(ocr.getExtractedText())
                .imageCount(ocr.getImageCount())
                .processingStatus(ocr.getProcessingStatus())
                .createdAt(ocr.getCreatedAt())
                .updatedAt(ocr.getUpdatedAt())
                .errorMessage(ocr.getErrorMessage())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponseDTO> healthCheck() {
        HealthCheckResponseDTO response = HealthCheckResponseDTO.builder()
                .status("healthy")
                .version("1.0.0")
                .build();

        return ResponseEntity.ok(response);
    }

    private int calculateProgress(DocumentModel documentModel) {
        if ("pending".equals(documentModel.getStatus())) {
            return 0;
        } else if ("processing".equals(documentModel.getStatus())) {
            return 50;
        } else if ("completed".equals(documentModel.getStatus())) {
            return 100;
        } else if ("failed".equals(documentModel.getStatus())) {
            return 0;
        }
        return 0;
    }

    private String getStatusMessage(String status) {
        return switch (status) {
            case "pending" -> "Document awaiting processing";
            case "processing" -> "Document processing";
            case "completed" -> "Document processed successfully";
            case "failed" -> "Error processing document";
            default -> "Unknown status";
        };
    }
}
