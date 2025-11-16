package com.clusterat.live.service;

import com.clusterat.live.dto.DocumentStatusEnum;
import com.clusterat.live.model.DocumentModel;
import com.clusterat.live.model.ProcessedImageModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DocProcessorService {
    private final Path documentsProcessedPath = Paths.get("documents_processed");
    private final Map<String, DocumentModel> documentStore = new ConcurrentHashMap<>();
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final OcrPreProcessingService ocrPreProcessingService;

    @Value("${python.processor.url:http://localhost:8000}")
    private String pythonProcessorUrl;
    @Value("${python.processor.api-key}")
    private String pythonProcessorApiKey;
    @Value("${document.cleanup.enabled:true}")
    private boolean cleanupEnabled;
    @Value("${document.cleanup.delay-minutes:30}")
    private int cleanupDelayMinutes;

    @Autowired
    public DocProcessorService(RestClient restClient, ObjectMapper objectMapper, OcrPreProcessingService ocrPreProcessingService) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.ocrPreProcessingService = ocrPreProcessingService;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(documentsProcessedPath);
            log.info("Processed documents directory created: {}", documentsProcessedPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Error creating processed documents directory", e);
            throw new RuntimeException("Failed to initialize processed documents directory", e);
        }
    }

    public DocumentModel processDocument(String filename, Path uploadedFilePath) {
        String documentId = UUID.randomUUID().toString();

        DocumentModel documentModel = com.clusterat.live.model.DocumentModel.builder()
                .id(documentId)
                .originalFilename(filename)
                .filePath(uploadedFilePath.toString())
                .status(DocumentStatusEnum.PENDING.getValue())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .processedImages(new ArrayList<>())
                .build();

        documentStore.put(documentId, documentModel);

        sendToProcessingAsync(documentId, uploadedFilePath, filename);

        return documentModel;
    }

    private void sendToProcessingAsync(String documentId, Path filePath, String filename) {
        new Thread(() -> {
            try {
                DocumentModel documentModel = documentStore.get(documentId);
                if (documentModel == null) {
                    return;
                }

                documentModel.setStatus(DocumentStatusEnum.PROCESSING.getValue());
                documentModel.setUpdatedAt(LocalDateTime.now());

                log.info("Sending document {} to Python service", documentId);

                sendFileToPythonService(documentId, filePath, filename);
                pollProcessingStatus(documentId);

            } catch (Exception e) {
                DocumentModel documentModel = documentStore.get(documentId);
                if (documentModel != null) {
                    documentModel.setStatus(DocumentStatusEnum.FAILED.getValue());
                    documentModel.setErrorMessage(e.getMessage());
                    documentModel.setUpdatedAt(LocalDateTime.now());
                }
                log.error("Error processing document {}: {}", documentId, e.getMessage(), e);
            }
        }).start();
    }

    private void sendFileToPythonService(String documentId, Path filePath, String filename) {
        try {
            log.debug("Sending file to Python: {} (documentId: {})", filename, documentId);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(filePath.toFile()));
            body.add("document_id", documentId);

            String response = restClient.post()
                    .uri(pythonProcessorUrl + "/api/v1/documents/process")
                    .header("X-API-KEY", pythonProcessorApiKey)
                    .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            log.info("Python service response for document {}: {}", documentId, response);

            try {
                JsonNode jsonNode = objectMapper.readTree(response);
                String status = jsonNode.get("status").asText();

                if ("completed".equals(status)) {
                    JsonNode imagesNode = jsonNode.get("processed_images");
                    List<ProcessedImageModel> processedImages = new ArrayList<>();

                    if (imagesNode != null && imagesNode.isArray()) {
                        for (JsonNode imageNode : imagesNode) {
                            ProcessedImageModel image = ProcessedImageModel.builder()
                                    .imageId(imageNode.get("image_id").asText())
                                    .imagePath(imageNode.get("image_path").asText())
                                    .dpi(imageNode.get("dpi").asInt())
                                    .format(imageNode.get("format").asText())
                                    .sizeKb(imageNode.get("size_kb").asDouble())
                                    .build();

                            processedImages.add(image);

                            downloadImageFromPython(documentId, image.getImageId());
                        }
                    }

                    DocumentModel documentModel = documentStore.get(documentId);
                    if (documentModel != null) {
                        documentModel.setProcessedImages(processedImages);
                        log.info("Processed images extracted from initial response for document {}: {} images",
                                documentId, processedImages.size());
                    }
                }
            } catch (Exception e) {
                log.warn("Error extracting processed images from Python response: {}", e.getMessage());
            }

        } catch (RestClientException e) {
            log.error("Error sending file to Python service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send file for processing", e);
        }
    }

    private void pollProcessingStatus(String documentId) {
        int maxAttempts = 10;
        int attempts = 0;
        int delayMs = 1000;

        while (attempts < maxAttempts) {
            try {
                DocumentModel documentModel = documentStore.get(documentId);
                if (documentModel == null) {
                    return;
                }

                String statusResponse = restClient.get()
                        .uri(pythonProcessorUrl + "/api/v1/documents/{id}/status", documentId)
                        .header("X-API-KEY", pythonProcessorApiKey)
                        .retrieve()
                        .body(String.class);

                JsonNode jsonNode = objectMapper.readTree(statusResponse);
                String status = jsonNode.get("status").asText();

                log.debug("Document {} status in Python service: {}", documentId, status);

                if ("completed".equals(status)) {
                    documentModel.setStatus(DocumentStatusEnum.COMPLETED.getValue());

                    if (documentModel.getProcessedImages() == null || documentModel.getProcessedImages().isEmpty()) {
                        fetchProcessedImages(documentId);
                    }

                    try {
                        log.info("Starting OCR processing for document {}", documentId);
                        ocrPreProcessingService.processDocumentImages(
                            documentId,
                                documentModel.getOriginalFilename(),
                                documentModel.getProcessedImages()
                        );
                    } catch (Exception e) {
                        log.error("Error starting OCR processing for document {}: {}", documentId, e.getMessage(), e);
                    }

                    documentModel.setUpdatedAt(LocalDateTime.now());
                    log.info("Document {} processed successfully", documentId);

                    scheduleMemoryCleanup(documentId);

                    return;

                } else if ("failed".equals(status)) {
                    documentModel.setStatus(DocumentStatusEnum.FAILED.getValue());
                    documentModel.setErrorMessage(jsonNode.get("message").asText());
                    documentModel.setUpdatedAt(LocalDateTime.now());
                    log.error("Document {} failed during processing", documentId);
                    return;
                }

                Thread.sleep(delayMs);
                attempts++;

            } catch (Exception e) {
                log.warn("Error checking document {} status: {}", documentId, e.getMessage());
                attempts++;
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        DocumentModel documentModel = documentStore.get(documentId);
        if (documentModel != null) {
            documentModel.setStatus(DocumentStatusEnum.FAILED.getValue());
            documentModel.setErrorMessage("Processing timeout for document");
            documentModel.setUpdatedAt(LocalDateTime.now());
        }
        log.error("Timeout processing document {}", documentId);
    }

    private void fetchProcessedImages(String documentId) {
        try {
            String processedResponse = restClient.get()
                    .uri(pythonProcessorUrl + "/api/v1/documents/{id}/status", documentId)
                    .header("X-API-KEY", pythonProcessorApiKey)
                    .retrieve()
                    .body(String.class);

            JsonNode jsonNode = objectMapper.readTree(processedResponse);
            JsonNode imagesNode = jsonNode.get("processed_images");

            List<ProcessedImageModel> processedImages = new ArrayList<>();

            if (imagesNode != null && imagesNode.isArray()) {
                for (JsonNode imageNode : imagesNode) {
                    ProcessedImageModel image = ProcessedImageModel.builder()
                            .imageId(imageNode.get("image_id").asText())
                            .imagePath(imageNode.get("image_path").asText())
                            .dpi(imageNode.get("dpi").asInt())
                            .format(imageNode.get("format").asText())
                            .sizeKb(imageNode.get("size_kb").asDouble())
                            .build();

                    processedImages.add(image);

                    downloadImageFromPython(documentId, image.getImageId());
                }
            }

            DocumentModel documentModel = documentStore.get(documentId);
            if (documentModel != null) {
                documentModel.setProcessedImages(processedImages);
            }

            log.info("Processed images fetched for document {}: {} images", documentId, processedImages.size());
        } catch (Exception e) {
            log.error("Error fetching processed images for document {}: {}", documentId, e.getMessage(), e);
        }
    }

    private void downloadImageFromPython(String documentId, String imageId) {
        try {
            byte[] imageData = restClient.get()
                    .uri(pythonProcessorUrl + "/api/v1/documents/{id}/download/{imgId}",
                            documentId, imageId)
                    .header("X-API-KEY", pythonProcessorApiKey)
                    .retrieve()
                    .body(byte[].class);

            if (imageData != null) {
                Path imagePath = documentsProcessedPath.resolve(imageId + ".png");
                Files.write(imagePath, imageData);
                log.debug("Image {} saved locally for document {}", imageId, documentId);
            }

        } catch (Exception e) {
            log.warn("Error downloading image {} from document {}: {}",
                    imageId, documentId, e.getMessage());
        }
    }

    public DocumentModel getDocumentStatus(String documentId) {
        return documentStore.get(documentId);
    }

    public byte[] downloadImage(String documentId, String imageId) throws IOException {
        DocumentModel documentModel = documentStore.get(documentId);
        if (documentModel == null) {
            throw new RuntimeException("Document not found: " + documentId);
        }

        Path imagePath = documentsProcessedPath.resolve(imageId + ".png");
        if (!Files.exists(imagePath)) {
            throw new RuntimeException("Image not found: " + imageId);
        }

        return Files.readAllBytes(imagePath);
    }

    private void scheduleMemoryCleanup(String documentId) {
        if (!cleanupEnabled) {
            log.info("Memory cleanup disabled for document {}", documentId);
            return;
        }

        new Thread(() -> {
            try {
                long delayMs = cleanupDelayMinutes * 60 * 1000L;
                log.info("Scheduling memory cleanup for document {} in {} minutes",
                        documentId, cleanupDelayMinutes);

                Thread.sleep(delayMs);

                cleanupDocument(documentId);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Memory cleanup interrupted for document {}", documentId);
            } catch (Exception e) {
                log.error("Error executing memory cleanup for document {}: {}",
                        documentId, e.getMessage(), e);
            }
        }).start();
    }

    public void cleanupDocument(String documentId) {
        log.info("Starting resource cleanup for document {}", documentId);

        DocumentModel documentModel = documentStore.get(documentId);
        if (documentModel == null) {
            log.warn("Document {} not found in store for cleanup", documentId);
            return;
        }

        try {
            if (documentModel.getProcessedImages() != null) {
                for (ProcessedImageModel image : documentModel.getProcessedImages()) {
                    try {
                        Path imagePath = documentsProcessedPath.resolve(image.getImageId() + ".png");
                        if (Files.exists(imagePath)) {
                            Files.delete(imagePath);
                            log.debug("Image deleted: {}", imagePath);
                        }
                    } catch (IOException e) {
                        log.warn("Error deleting image {}: {}", image.getImageId(), e.getMessage());
                    }
                }
            }

            try {
                Path uploadPath = Paths.get(documentModel.getFilePath());
                if (Files.exists(uploadPath)) {
                    Files.delete(uploadPath);
                    log.debug("Original file deleted: {}", uploadPath);
                }
            } catch (IOException e) {
                log.warn("Error deleting original file {}: {}", documentModel.getFilePath(), e.getMessage());
            }

            documentStore.remove(documentId);

            System.gc();

            log.info("Resource cleanup completed for document {}", documentId);
        } catch (Exception e) {
            log.error("Error cleaning up resources for document {}: {}", documentId, e.getMessage(), e);
        }
    }

    public void cleanupOldDocuments(int maxAgeMinutes) {
        log.info("Cleaning up documents older than {} minutes", maxAgeMinutes);

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(maxAgeMinutes);
        int cleanedCount = 0;

        for (Map.Entry<String, DocumentModel> entry : documentStore.entrySet()) {
            DocumentModel doc = entry.getValue();
            if (doc.getUpdatedAt().isBefore(cutoffTime)) {
                cleanupDocument(doc.getId());
                cleanedCount++;
            }
        }

        log.info("Automatic cleanup completed: {} documents removed", cleanedCount);
    }
}
