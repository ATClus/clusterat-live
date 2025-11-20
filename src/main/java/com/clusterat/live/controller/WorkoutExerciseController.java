package com.clusterat.live.controller;

import com.clusterat.live.dto.AnalysisResponseDTO;
import com.clusterat.live.dto.WorkoutExerciseDTO;
import com.clusterat.live.service.WorkoutExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/workout/exercises")
@RequiredArgsConstructor
public class WorkoutExerciseController {
    private final WorkoutExerciseService workoutExerciseService;

    @GetMapping
    public ResponseEntity<AnalysisResponseDTO> listAll() {
        List<WorkoutExerciseDTO> exercises = workoutExerciseService.listAll();
        return ResponseEntity.ok(AnalysisResponseDTO.builder()
                .success(true)
                .message("Exercises fetched")
                .data(exercises)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> get(@PathVariable Long id) {
        try {
            WorkoutExerciseDTO exercise = workoutExerciseService.getById(id);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Exercise fetched")
                    .data(exercise)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<AnalysisResponseDTO> create(@RequestBody WorkoutExerciseDTO dto) {
        WorkoutExerciseDTO created = workoutExerciseService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AnalysisResponseDTO.builder()
                        .success(true)
                        .message("Exercise created")
                        .data(created)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody WorkoutExerciseDTO dto) {
        try {
            WorkoutExerciseDTO updated = workoutExerciseService.update(id, dto);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Exercise updated")
                    .data(updated)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> delete(@PathVariable Long id) {
        try {
            workoutExerciseService.delete(id);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Exercise deleted")
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }
}

