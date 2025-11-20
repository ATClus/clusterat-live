package com.clusterat.live.dto;

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
public class WorkoutSetDTO {
    private Long id;
    private Long exerciseId;
    private Integer setOrder;
    private Integer reps;
    private BigDecimal weightKg;
    private Integer rpe;
    private Integer restSeconds;
    private Boolean warmup;
    private OffsetDateTime performedAt;
}

