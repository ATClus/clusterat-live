package com.clusterat.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSessionDTO {
    private UUID id;
    private Long userId;
    private Long templateId;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private String observation;
    private List<WorkoutSetDTO> sets;
}

