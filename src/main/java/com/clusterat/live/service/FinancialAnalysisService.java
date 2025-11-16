package com.clusterat.live.service;

import com.clusterat.live.dto.FinancialAnalysisDTO;
import com.clusterat.live.model.AnalysisType;
import com.clusterat.live.model.FinancialAnalysisModel;
import com.clusterat.live.repository.FinancialAnalysisRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FinancialAnalysisService {
    private final FinancialAnalysisRepository financialAnalysisRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public FinancialAnalysisService(FinancialAnalysisRepository financialAnalysisRepository, ObjectMapper objectMapper) {
        this.financialAnalysisRepository = financialAnalysisRepository;
        this.objectMapper = objectMapper;
    }

    public List<FinancialAnalysisDTO> getAllAnalysis() {
        log.info("Fetching all financial analysis records");
        return financialAnalysisRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<FinancialAnalysisDTO> getAnalysisById(Long analysisId) {
        log.info("Fetching financial analysis with id: {}", analysisId);
        return financialAnalysisRepository.findById(analysisId)
                .map(this::convertToDTO);
    }

    public List<FinancialAnalysisDTO> getAnalysisByCategory(Integer categoryId) {
        log.info("Fetching financial analysis by category: {}", categoryId);
        return financialAnalysisRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FinancialAnalysisDTO> getAnalysisByType(String analysisType) {
        log.info("Fetching financial analysis by type: {}", analysisType);
        try {
            AnalysisType typeEnum = AnalysisType.fromValue(analysisType);
            return financialAnalysisRepository.findByAnalysisType(typeEnum).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid analysis type '{}': {}", analysisType, e.getMessage());
            return List.of();
        }
    }

    public FinancialAnalysisDTO saveAnalysis(FinancialAnalysisDTO analysisDTO) {
        log.info("Saving new financial analysis: {}", analysisDTO);
        FinancialAnalysisModel model = convertToModel(analysisDTO);
        model.setCreatedAt(OffsetDateTime.now());
        FinancialAnalysisModel saved = financialAnalysisRepository.save(model);
        log.info("Financial analysis saved with id: {}", saved.getAnalysisId());
        return convertToDTO(saved);
    }

    public List<FinancialAnalysisDTO> saveAnalysisBulk(List<FinancialAnalysisDTO> analysisDTOs) {
        log.info("Saving {} financial analysis records", analysisDTOs.size());
        return analysisDTOs.stream()
                .map(this::saveAnalysis)
                .collect(Collectors.toList());
    }

    public FinancialAnalysisDTO updateAnalysis(Long analysisId, FinancialAnalysisDTO analysisDTO) {
        log.info("Updating financial analysis with id: {}", analysisId);
        Optional<FinancialAnalysisModel> existing = financialAnalysisRepository.findById(analysisId);

        if (existing.isEmpty()) {
            log.warn("Financial analysis not found with id: {}", analysisId);
            throw new RuntimeException("Financial analysis not found with id: " + analysisId);
        }

        FinancialAnalysisModel model = existing.get();
        updateModelFields(model, analysisDTO);
        FinancialAnalysisModel updated = financialAnalysisRepository.save(model);
        log.info("Financial analysis updated with id: {}", updated.getAnalysisId());
        return convertToDTO(updated);
    }

    public void deleteAnalysis(Long analysisId) {
        log.info("Deleting financial analysis with id: {}", analysisId);
        if (!financialAnalysisRepository.existsById(analysisId)) {
            log.warn("Financial analysis not found with id: {}", analysisId);
            throw new RuntimeException("Financial analysis not found with id: " + analysisId);
        }
        financialAnalysisRepository.deleteById(analysisId);
        log.info("Financial analysis deleted with id: {}", analysisId);
    }

    private FinancialAnalysisDTO convertToDTO(FinancialAnalysisModel model) {
        JsonNode metadataNode = null;
        if (model.getMetadata() != null) {
            try {
                metadataNode = objectMapper.readTree(model.getMetadata());
            } catch (Exception e) {
                log.warn("Failed to parse metadata JSON, using null: {}", e.getMessage());
            }
        }

        return FinancialAnalysisDTO.builder()
                .analysisId(model.getAnalysisId())
                .sourceTransactionId(model.getSourceTransactionId())
                .amount(model.getAmount())
                .transactionDate(model.getTransactionDate())
                .description(model.getDescription())
                .categoryId(model.getCategoryId())
                .analysisType(model.getAnalysisType() != null ? model.getAnalysisType().getValue() : null)
                .analysisNotes(model.getAnalysisNotes())
                .metadata(metadataNode)
                .createdAt(model.getCreatedAt())
                .build();
    }

    private FinancialAnalysisModel convertToModel(FinancialAnalysisDTO dto) {
        String metadataString = null;
        if (dto.getMetadata() != null) {
            try {
                metadataString = objectMapper.writeValueAsString(dto.getMetadata());
            } catch (Exception e) {
                log.warn("Failed to serialize metadata JSON, using null: {}", e.getMessage());
            }
        }

        AnalysisType analysisTypeEnum = null;
        if (dto.getAnalysisType() != null) {
            try {
                analysisTypeEnum = AnalysisType.fromValue(dto.getAnalysisType());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid analysis type '{}', using null: {}", dto.getAnalysisType(), e.getMessage());
            }
        }

        return FinancialAnalysisModel.builder()
                .analysisId(dto.getAnalysisId())
                .sourceTransactionId(dto.getSourceTransactionId())
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .description(dto.getDescription())
                .categoryId(dto.getCategoryId())
                .analysisType(analysisTypeEnum)
                .analysisNotes(dto.getAnalysisNotes())
                .metadata(metadataString)
                .createdAt(dto.getCreatedAt())
                .build();
    }

    private void updateModelFields(FinancialAnalysisModel model, FinancialAnalysisDTO dto) {
        if (dto.getSourceTransactionId() != null) {
            model.setSourceTransactionId(dto.getSourceTransactionId());
        }
        if (dto.getAmount() != null) {
            model.setAmount(dto.getAmount());
        }
        if (dto.getTransactionDate() != null) {
            model.setTransactionDate(dto.getTransactionDate());
        }
        if (dto.getDescription() != null) {
            model.setDescription(dto.getDescription());
        }
        if (dto.getCategoryId() != null) {
            model.setCategoryId(dto.getCategoryId());
        }
        if (dto.getAnalysisType() != null) {
            try {
                model.setAnalysisType(AnalysisType.fromValue(dto.getAnalysisType()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid analysis type '{}' during update: {}", dto.getAnalysisType(), e.getMessage());
            }
        }
        if (dto.getAnalysisNotes() != null) {
            model.setAnalysisNotes(dto.getAnalysisNotes());
        }
        if (dto.getMetadata() != null) {
            try {
                model.setMetadata(objectMapper.writeValueAsString(dto.getMetadata()));
            } catch (Exception e) {
                log.warn("Failed to serialize metadata JSON during update: {}", e.getMessage());
            }
        }
    }
}

