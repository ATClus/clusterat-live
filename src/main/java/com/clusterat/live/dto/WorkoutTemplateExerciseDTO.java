package com.clusterat.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutTemplateExerciseDTO {
    private Long id;
    private Long templateId;
    private Long exerciseId;
    private Integer displayOrder;
    private Integer targetSets;
    private String targetReps;
    private Integer targetRestSec;
    private WorkoutExerciseDTO exercise;
}
