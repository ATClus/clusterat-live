package com.clusterat.live.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutTemplateDTO {
    private Long id;
    private String name;
    private Long createdByUserId;
    private List<WorkoutTemplateExerciseDTO> exercises;
}

