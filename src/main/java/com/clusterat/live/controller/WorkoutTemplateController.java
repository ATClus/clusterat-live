package com.clusterat.live.controller;

import com.clusterat.live.dto.AnalysisResponseDTO;
import com.clusterat.live.dto.WorkoutTemplateDTO;
import com.clusterat.live.service.WorkoutTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/workout/templates")
@RequiredArgsConstructor
public class WorkoutTemplateController {
    private final WorkoutTemplateService workoutTemplateService;

    @GetMapping
    public ResponseEntity<AnalysisResponseDTO> listAll() {
        List<WorkoutTemplateDTO> templates = workoutTemplateService.listAll();
        return ResponseEntity.ok(AnalysisResponseDTO.builder()
                .success(true)
                .message("Templates fetched")
                .data(templates)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> get(@PathVariable Long id) {
        try {
            WorkoutTemplateDTO template = workoutTemplateService.getById(id);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Template fetched")
                    .data(template)
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
    public ResponseEntity<AnalysisResponseDTO> create(@RequestBody WorkoutTemplateDTO dto) {
        WorkoutTemplateDTO created = workoutTemplateService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AnalysisResponseDTO.builder()
                        .success(true)
                        .message("Template created")
                        .data(created)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody WorkoutTemplateDTO dto) {
        try {
            WorkoutTemplateDTO updated = workoutTemplateService.update(id, dto);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Template updated")
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
            workoutTemplateService.delete(id);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Template deleted")
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

