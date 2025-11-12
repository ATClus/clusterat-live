package com.clusterat.live.service;

import com.clusterat.live.model.OcrPreProcessingModel;
import com.clusterat.live.model.ProcessedImageModel;
import com.clusterat.live.repository.OcrPreProcessingRepository;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OcrPreProcessingService {
    private final OcrPreProcessingRepository ocrPreProcessingRepository;
    private final OcrService ocrService;
    private final Path documentsProcessedPath = Paths.get("documents_processed");

    @Autowired
    public OcrPreProcessingService(OcrPreProcessingRepository ocrPreProcessingRepository, OcrService ocrService) {
        this.ocrPreProcessingRepository = ocrPreProcessingRepository;
        this.ocrService = ocrService;
    }

    @Transactional
    public OcrPreProcessingModel processDocumentImages(String documentId, String documentName, List<ProcessedImageModel> processedImages) {
        log.info("Starting OCR processing for document: {} ({})", documentId, documentName);

        Optional<OcrPreProcessingModel> existingRecord = ocrPreProcessingRepository.findByDocumentId(documentId);

        OcrPreProcessingModel ocrPreProcessingModel;
        if (existingRecord.isPresent()) {
            ocrPreProcessingModel = existingRecord.get();
            ocrPreProcessingModel.setProcessingStatus("processing");
            ocrPreProcessingModel.setUpdatedAt(LocalDateTime.now());
        } else {
            ocrPreProcessingModel = OcrPreProcessingModel.builder()
                    .documentId(documentId)
                    .documentName(documentName)
                    .imageCount(processedImages != null ? processedImages.size() : 0)
                    .processingStatus("processing")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        ocrPreProcessingRepository.save(ocrPreProcessingModel);

        processImagesAsync(documentId, documentName, processedImages);

        return ocrPreProcessingModel;
    }

    private void processImagesAsync(String documentId, String documentName, List<ProcessedImageModel> processedImages) {
        new Thread(() -> {
            try {
                StringBuilder extractedText = new StringBuilder();

                if (processedImages != null && !processedImages.isEmpty()) {
                    log.info("Processing {} images for document {}", processedImages.size(), documentId);

                    for (int i = 0; i < processedImages.size(); i++) {
                        ProcessedImageModel image = processedImages.get(i);
                        String imageId = image.getImageId();

                        try {
                            Path imagePath = documentsProcessedPath.resolve(imageId + ".png");

                            if (!Files.exists(imagePath)) {
                                log.warn("Image not found: {}", imagePath);
                                continue;
                            }

                            log.debug("Extracting text from image {} ({}/{})", imageId, i + 1, processedImages.size());

                            String text = ocrService.extractText(imagePath.toString());

                            if (text != null && !text.trim().isEmpty()) {
                                extractedText.append("=== Page ").append(i + 1).append(" ===\n");
                                extractedText.append(text.trim()).append("\n\n");
                            }

                            text = null;
                            System.gc();

                        } catch (TesseractException e) {
                            log.error("Error processing OCR on image {}: {}", imageId, e.getMessage());
                        }
                    }
                }

                OcrPreProcessingModel ocrPreProcessingModel = ocrPreProcessingRepository.findByDocumentId(documentId)
                        .orElseThrow(() -> new RuntimeException("OCR record not found for document: " + documentId));

                ocrPreProcessingModel.setExtractedText(extractedText.toString());
                ocrPreProcessingModel.setProcessingStatus("completed");
                ocrPreProcessingModel.setUpdatedAt(LocalDateTime.now());
                ocrPreProcessingRepository.save(ocrPreProcessingModel);

                log.info("OCR processing completed for document {}: {} characters extracted",
                        documentId, extractedText.length());

                extractedText = null;
                System.gc();

            } catch (Exception e) {
                log.error("Error processing images for document {}: {}", documentId, e.getMessage(), e);

                try {
                    OcrPreProcessingModel ocrPreProcessingModel = ocrPreProcessingRepository.findByDocumentId(documentId)
                            .orElseThrow(() -> new RuntimeException("OCR record not found for document: " + documentId));

                    ocrPreProcessingModel.setProcessingStatus("failed");
                    ocrPreProcessingModel.setErrorMessage(e.getMessage());
                    ocrPreProcessingModel.setUpdatedAt(LocalDateTime.now());
                    ocrPreProcessingRepository.save(ocrPreProcessingModel);
                } catch (Exception ex) {
                    log.error("Error updating failure status: {}", ex.getMessage());
                }
            }
        }).start();
    }

    public Optional<OcrPreProcessingModel> getOcrResult(String documentId) {
        return ocrPreProcessingRepository.findByDocumentId(documentId);
    }

    public List<OcrPreProcessingModel> getAllOcrRecords() {
        return ocrPreProcessingRepository.findAll();
    }
}
