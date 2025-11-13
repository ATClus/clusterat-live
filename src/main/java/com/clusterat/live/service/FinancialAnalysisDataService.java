package com.clusterat.live.service;

import com.clusterat.live.dto.OcrPreProcessingDTO;
import com.clusterat.live.model.OcrPreProcessingModel;
import com.clusterat.live.repository.OcrPreProcessingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FinancialAnalysisDataService {
    private final OcrPreProcessingRepository ocrPreProcessingRepository;

    @Autowired
    public FinancialAnalysisDataService(OcrPreProcessingRepository ocrPreProcessingRepository) {
        this.ocrPreProcessingRepository = ocrPreProcessingRepository;
    }

    public List<OcrPreProcessingDTO> getAllOcrPreProcessingData() {
        log.info("Fetching all OCR pre-processing data");
        return ocrPreProcessingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<OcrPreProcessingDTO> getOcrPreProcessingById(String id) {
        log.info("Fetching OCR pre-processing data with id: {}", id);
        return ocrPreProcessingRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<OcrPreProcessingDTO> getOcrPreProcessingByDocumentId(String documentId) {
        log.info("Fetching OCR pre-processing data with document id: {}", documentId);
        return ocrPreProcessingRepository.findByDocumentId(documentId)
                .map(this::convertToDTO);
    }

    private OcrPreProcessingDTO convertToDTO(OcrPreProcessingModel model) {
        return OcrPreProcessingDTO.builder()
                .id(model.getId())
                .documentId(model.getDocumentId())
                .documentName(model.getDocumentName())
                .extractedText(model.getExtractedText())
                .imageCount(model.getImageCount())
                .processingStatus(model.getProcessingStatus())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .errorMessage(model.getErrorMessage())
                .build();
    }
}

