package com.clusterat.live.controller;

import com.clusterat.live.dto.AnalysisResponseDTO;
import com.clusterat.live.dto.WorkoutSessionDTO;
import com.clusterat.live.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/workout/sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {
    private final WorkoutSessionService workoutSessionService;

    @GetMapping
    public ResponseEntity<AnalysisResponseDTO> list(@RequestParam Long userId) {
        List<WorkoutSessionDTO> sessions = workoutSessionService.listSessions(userId);
        return ResponseEntity.ok(AnalysisResponseDTO.builder()
                .success(true)
                .message("Sessions fetched")
                .data(sessions)
                .build());
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<AnalysisResponseDTO> get(@PathVariable UUID sessionId) {
        try {
            WorkoutSessionDTO session = workoutSessionService.getSession(sessionId);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Session fetched")
                    .data(session)
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
    public ResponseEntity<AnalysisResponseDTO> create(@RequestBody WorkoutSessionDTO dto) {
        WorkoutSessionDTO created = workoutSessionService.createSession(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AnalysisResponseDTO.builder()
                        .success(true)
                        .message("Session created")
                        .data(created)
                        .build());
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<AnalysisResponseDTO> update(@PathVariable UUID sessionId,
                                                      @RequestBody WorkoutSessionDTO dto) {
        try {
            WorkoutSessionDTO updated = workoutSessionService.updateSession(sessionId, dto);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Session updated")
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

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<AnalysisResponseDTO> delete(@PathVariable UUID sessionId) {
        try {
            workoutSessionService.deleteSession(sessionId);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("Session deleted")
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

