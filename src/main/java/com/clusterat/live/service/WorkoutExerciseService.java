package com.clusterat.live.service;

import com.clusterat.live.dto.WorkoutExerciseDTO;
import com.clusterat.live.model.WorkoutExerciseModel;
import com.clusterat.live.repository.WorkoutExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutExerciseService {
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public List<WorkoutExerciseDTO> listAll() {
        return workoutExerciseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WorkoutExerciseDTO getById(Long id) {
        return workoutExerciseRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    @Transactional
    public WorkoutExerciseDTO create(WorkoutExerciseDTO dto) {
        WorkoutExerciseModel entity = toEntity(dto);
        return toDTO(workoutExerciseRepository.save(entity));
    }

    @Transactional
    public WorkoutExerciseDTO update(Long id, WorkoutExerciseDTO dto) {
        WorkoutExerciseModel entity = workoutExerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setMuscleGroup(dto.getMuscleGroup());
        entity.setLinkExecution(dto.getLinkExecution());
        return toDTO(workoutExerciseRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!workoutExerciseRepository.existsById(id)) {
            throw new IllegalArgumentException("Exercise not found");
        }
        workoutExerciseRepository.deleteById(id);
    }

    private WorkoutExerciseDTO toDTO(WorkoutExerciseModel model) {
        return WorkoutExerciseDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .muscleGroup(model.getMuscleGroup())
                .linkExecution(model.getLinkExecution())
                .build();
    }

    private WorkoutExerciseModel toEntity(WorkoutExerciseDTO dto) {
        return WorkoutExerciseModel.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .muscleGroup(dto.getMuscleGroup())
                .linkExecution(dto.getLinkExecution())
                .build();
    }
}

