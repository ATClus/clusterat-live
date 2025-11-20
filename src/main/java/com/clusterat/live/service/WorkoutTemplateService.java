package com.clusterat.live.service;

import com.clusterat.live.dto.WorkoutExerciseDTO;
import com.clusterat.live.dto.WorkoutTemplateDTO;
import com.clusterat.live.dto.WorkoutTemplateExerciseDTO;
import com.clusterat.live.model.WorkoutTemplateExerciseModel;
import com.clusterat.live.model.WorkoutTemplateModel;
import com.clusterat.live.repository.WorkoutExerciseRepository;
import com.clusterat.live.repository.WorkoutTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutTemplateService {
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public List<WorkoutTemplateDTO> listAll() {
        return workoutTemplateRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WorkoutTemplateDTO getById(Long id) {
        return workoutTemplateRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
    }

    @Transactional
    public WorkoutTemplateDTO create(WorkoutTemplateDTO dto) {
        WorkoutTemplateModel template = new WorkoutTemplateModel();
        template.setName(dto.getName());
        template.setCreatedByUserId(dto.getCreatedByUserId());

        if (dto.getExercises() != null) {
            dto.getExercises().forEach(exDto -> template.addExercise(toExerciseEntity(exDto)));
        }

        return toDTO(workoutTemplateRepository.save(template));
    }

    @Transactional
    public WorkoutTemplateDTO update(Long id, WorkoutTemplateDTO dto) {
        WorkoutTemplateModel template = workoutTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        template.setName(dto.getName());
        template.setCreatedByUserId(dto.getCreatedByUserId());
        template.getExercises().clear();
        if (dto.getExercises() != null) {
            dto.getExercises().forEach(exDto -> template.addExercise(toExerciseEntity(exDto)));
        }

        return toDTO(workoutTemplateRepository.save(template));
    }

    @Transactional
    public void delete(Long id) {
        if (!workoutTemplateRepository.existsById(id)) {
            throw new IllegalArgumentException("Template not found");
        }
        workoutTemplateRepository.deleteById(id);
    }

    private WorkoutTemplateDTO toDTO(WorkoutTemplateModel model) {
        return WorkoutTemplateDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .createdByUserId(model.getCreatedByUserId())
                .exercises(model.getExercises().stream()
                        .sorted(Comparator.comparingInt(WorkoutTemplateExerciseModel::getDisplayOrder))
                        .map(this::toExerciseDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private WorkoutTemplateExerciseDTO toExerciseDTO(WorkoutTemplateExerciseModel model) {
        return WorkoutTemplateExerciseDTO.builder()
                .id(model.getId())
                .templateId(model.getTemplate().getId())
                .exerciseId(model.getExercise().getId())
                .displayOrder(model.getDisplayOrder())
                .targetSets(model.getTargetSets())
                .targetReps(model.getTargetReps())
                .targetRestSec(model.getTargetRestSec())
                .exercise(WorkoutExerciseDTO.builder()
                        .id(model.getExercise().getId())
                        .name(model.getExercise().getName())
                        .description(model.getExercise().getDescription())
                        .muscleGroup(model.getExercise().getMuscleGroup())
                        .linkExecution(model.getExercise().getLinkExecution())
                        .build())
                .build();
    }

    private WorkoutTemplateExerciseModel toExerciseEntity(WorkoutTemplateExerciseDTO dto) {
        WorkoutTemplateExerciseModel entity = new WorkoutTemplateExerciseModel();
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setTargetSets(dto.getTargetSets());
        entity.setTargetReps(dto.getTargetReps());
        entity.setTargetRestSec(dto.getTargetRestSec());
        entity.setExercise(workoutExerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found")));
        return entity;
    }
}
