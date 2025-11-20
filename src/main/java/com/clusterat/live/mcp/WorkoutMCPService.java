package com.clusterat.live.mcp;

import com.clusterat.live.dto.WorkoutSessionDTO;
import com.clusterat.live.dto.WorkoutSetDTO;
import com.clusterat.live.dto.WorkoutTemplateDTO;
import com.clusterat.live.dto.WorkoutTemplateExerciseDTO;
import com.clusterat.live.service.WorkoutSessionService;
import com.clusterat.live.service.WorkoutTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutMCPService {
    private final WorkoutSessionService workoutSessionService;
    private final WorkoutTemplateService workoutTemplateService;

    @Tool(description = "Start a workout session for a user and optionally attach a template.")
    public ResponseEntity<WorkoutSessionDTO> startWorkoutSession(Long userId, Long templateId, String observation) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            WorkoutSessionDTO request = WorkoutSessionDTO.builder()
                    .userId(userId)
                    .templateId(templateId)
                    .observation(observation)
                    .startedAt(OffsetDateTime.now())
                    .build();
            WorkoutSessionDTO created = workoutSessionService.createSession(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            log.warn("Unable to start session: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error while starting session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Tool(description = "Finish a workout session by stamping the end time.")
    public ResponseEntity<WorkoutSessionDTO> endWorkoutSession(UUID sessionId) {
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            WorkoutSessionDTO session = workoutSessionService.getSession(sessionId);
            session.setEndedAt(OffsetDateTime.now());
            WorkoutSessionDTO updated = workoutSessionService.updateSession(sessionId, session);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            log.warn("Session not found for completion: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error when ending session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Tool(description = "Retrieve the ordered exercises for a chosen template.")
    public ResponseEntity<List<WorkoutTemplateExerciseDTO>> getTemplateExercises(Long templateId) {
        if (templateId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            WorkoutTemplateDTO template = workoutTemplateService.getById(templateId);
            List<WorkoutTemplateExerciseDTO> exercises = template.getExercises();
            return ResponseEntity.ok(exercises == null ? Collections.emptyList() : exercises);
        } catch (IllegalArgumentException ex) {
            log.warn("Template not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error retrieving template exercises", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Tool(description = "Replace the executed set list for a session.")
    public ResponseEntity<WorkoutSessionDTO> updateSessionSets(UUID sessionId, List<WorkoutSetDTO> sets) {
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<WorkoutSetDTO> normalizedSets = sets == null ? Collections.emptyList() : sets;
        normalizedSets.forEach(set -> {
            if (set.getPerformedAt() == null) {
                set.setPerformedAt(OffsetDateTime.now());
            }
            if (set.getWarmup() == null) {
                set.setWarmup(Boolean.FALSE);
            }
        });
        try {
            WorkoutSessionDTO session = workoutSessionService.getSession(sessionId);
            session.setSets(normalizedSets);
            WorkoutSessionDTO updated = workoutSessionService.updateSession(sessionId, session);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to update sets for session {}: {}", sessionId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error updating session sets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
