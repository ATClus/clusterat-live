package com.clusterat.live.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAnalysisDTO {
    private Long analysisId;
    private String sourceTransactionId;
    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX][X]")
    private OffsetDateTime transactionDate;
    private String description;
    private Integer categoryId;
    private String analysisType;
    private String analysisNotes;
    private JsonNode metadata;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX][X]")
    private OffsetDateTime createdAt;
}

