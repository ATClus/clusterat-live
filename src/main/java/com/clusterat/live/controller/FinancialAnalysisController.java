package com.clusterat.live.controller;

import com.clusterat.live.dto.AnalysisResponseDTO;
import com.clusterat.live.dto.FinancialAnalysisDTO;
import com.clusterat.live.dto.OcrPreProcessingDTO;
import com.clusterat.live.service.FinancialAnalysisDataService;
import com.clusterat.live.service.FinancialAnalysisService;
import com.clusterat.live.service.OcrPreProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/financial-analysis")
public class FinancialAnalysisController {
    private final FinancialAnalysisService financialAnalysisService;
    private final FinancialAnalysisDataService financialAnalysisDataService;
    private final OcrPreProcessingService ocrPreProcessingService;

    @Autowired
    public FinancialAnalysisController(
            FinancialAnalysisService financialAnalysisService,
            FinancialAnalysisDataService financialAnalysisDataService,
            OcrPreProcessingService ocrPreProcessingService) {
        this.financialAnalysisService = financialAnalysisService;
        this.financialAnalysisDataService = financialAnalysisDataService;
        this.ocrPreProcessingService = ocrPreProcessingService;
    }

    @GetMapping("/ocr-data")
    public ResponseEntity<AnalysisResponseDTO> getOcrPreProcessingData() {
        log.info("GET request to fetch OCR pre-processing data");
        try {
            List<OcrPreProcessingDTO> data = financialAnalysisDataService.getAllOcrPreProcessingData();
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("OCR pre-processing data retrieved successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching OCR pre-processing data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching OCR pre-processing data")
                            .build());
        }
    }

    @GetMapping("/ocr-data/{id}")
    public ResponseEntity<AnalysisResponseDTO> getOcrPreProcessingDataById(@PathVariable String id) {
        log.info("GET request to fetch OCR pre-processing data by id: {}", id);
        try {
            Optional<OcrPreProcessingDTO> data = financialAnalysisDataService.getOcrPreProcessingById(id);
            return data.map(ocrPreProcessingDTO -> ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("OCR pre-processing data retrieved successfully")
                    .data(ocrPreProcessingDTO)
                    .build())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("OCR pre-processing data not found")
                            .build()));
        } catch (Exception e) {
            log.error("Error fetching OCR pre-processing data by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching OCR pre-processing data")
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<AnalysisResponseDTO> getAllAnalysis() {
        log.info("GET request to fetch all financial analysis records");
        try {
            List<FinancialAnalysisDTO> data = financialAnalysisService.getAllAnalysis();
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Financial analysis records retrieved successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching financial analysis records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching financial analysis records")
                            .build());
        }
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponseDTO> getAnalysisById(@PathVariable Long analysisId) {
        log.info("GET request to fetch financial analysis by id: {}", analysisId);
        try {
            Optional<FinancialAnalysisDTO> data = financialAnalysisService.getAnalysisById(analysisId);
            if (data.isPresent()) {
                return ResponseEntity.ok(AnalysisResponseDTO.builder()
                        .success(true)
                        .message("Financial analysis retrieved successfully")
                        .data(data.get())
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(AnalysisResponseDTO.builder()
                                .success(false)
                                .message("Financial analysis not found")
                                .build());
            }
        } catch (Exception e) {
            log.error("Error fetching financial analysis by id: {}", analysisId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching financial analysis")
                            .build());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<AnalysisResponseDTO> getAnalysisByCategory(@PathVariable Integer categoryId) {
        log.info("GET request to fetch financial analysis by category: {}", categoryId);
        try {
            List<FinancialAnalysisDTO> data = financialAnalysisService.getAnalysisByCategory(categoryId);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Financial analysis records retrieved successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching financial analysis by category: {}", categoryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching financial analysis by category")
                            .build());
        }
    }

    @GetMapping("/type/{analysisType}")
    public ResponseEntity<AnalysisResponseDTO> getAnalysisByType(@PathVariable String analysisType) {
        log.info("GET request to fetch financial analysis by type: {}", analysisType);
        try {
            List<FinancialAnalysisDTO> data = financialAnalysisService.getAnalysisByType(analysisType);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Financial analysis records retrieved successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            log.error("Error fetching financial analysis by type: {}", analysisType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error fetching financial analysis by type")
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<AnalysisResponseDTO> saveAnalysis(@RequestBody FinancialAnalysisDTO analysisDTO) {
        log.info("POST request to save financial analysis: {}", analysisDTO);
        try {
            if (analysisDTO.getAmount() == null || analysisDTO.getCategoryId() == null ||
                analysisDTO.getTransactionDate() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(AnalysisResponseDTO.builder()
                                .success(false)
                                .message("Missing required fields: amount, categoryId, and transactionDate are mandatory")
                                .build());
            }

            FinancialAnalysisDTO saved = financialAnalysisService.saveAnalysis(analysisDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AnalysisResponseDTO.builder()
                            .success(true)
                            .message("Financial analysis saved successfully")
                            .data(saved)
                            .build());
        } catch (Exception e) {
            log.error("Error saving financial analysis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error saving financial analysis: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponseDTO> updateAnalysis(
            @PathVariable Long analysisId,
            @RequestBody FinancialAnalysisDTO analysisDTO) {
        log.info("PUT request to update financial analysis with id: {}", analysisId);
        try {
            FinancialAnalysisDTO updated = financialAnalysisService.updateAnalysis(analysisId, analysisDTO);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Financial analysis updated successfully")
                    .data(updated)
                    .build());
        } catch (RuntimeException e) {
            log.warn("Financial analysis not found: {}", analysisId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Financial analysis not found")
                            .build());
        } catch (Exception e) {
            log.error("Error updating financial analysis with id: {}", analysisId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error updating financial analysis: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<AnalysisResponseDTO> deleteAnalysis(@PathVariable Long analysisId) {
        log.info("DELETE request to delete financial analysis with id: {}", analysisId);
        try {
            financialAnalysisService.deleteAnalysis(analysisId);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Financial analysis deleted successfully")
                    .build());
        } catch (RuntimeException e) {
            log.warn("Financial analysis not found: {}", analysisId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Financial analysis not found")
                            .build());
        } catch (Exception e) {
            log.error("Error deleting financial analysis with id: {}", analysisId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message("Error deleting financial analysis: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/ocr-pre-processing/{ocrId}")
    public ResponseEntity<OcrPreProcessingDTO> deleteOCRPreProcessedData(@PathVariable String ocrId) {
        log.info("DELETE request to delete OCR pre-processing data with id or document_id: {}", ocrId);
        try {
            ocrPreProcessingService.deleteOcrPreProcessing(ocrId);
            return ResponseEntity.ok(OcrPreProcessingDTO.builder()
                    .success(true)
                    .message("OCR pre-processing data deleted successfully")
                    .build());
        } catch (RuntimeException e) {
            log.warn("OCR pre-processing data not found: {}", ocrId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcrPreProcessingDTO.builder()
                            .success(false)
                            .message("OCR pre-processing data not found")
                            .build());
        } catch (Exception e) {
            log.error("Error deleting OCR pre-processing data with id or document_id: {}", ocrId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(OcrPreProcessingDTO.builder()
                            .success(false)
                            .message("Error deleting OCR pre-processing data: " + e.getMessage())
                            .build());
        }
    }
}

