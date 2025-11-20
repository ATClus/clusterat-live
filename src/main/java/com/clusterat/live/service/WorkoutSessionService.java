package com.clusterat.live.service;

import com.clusterat.live.dto.WorkoutSessionDTO;
import com.clusterat.live.dto.WorkoutSetDTO;
import com.clusterat.live.model.WorkoutExerciseModel;
import com.clusterat.live.model.WorkoutSessionModel;
import com.clusterat.live.model.WorkoutSetModel;
import com.clusterat.live.model.WorkoutTemplateModel;
import com.clusterat.live.repository.WorkoutExerciseRepository;
import com.clusterat.live.repository.WorkoutSessionRepository;
import com.clusterat.live.repository.WorkoutSetRepository;
import com.clusterat.live.repository.WorkoutTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;

    public List<WorkoutSessionDTO> listSessions(Long userId) {
        return workoutSessionRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WorkoutSessionDTO getSession(UUID sessionId) {
        return workoutSessionRepository.findById(sessionId)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    @Transactional
    public WorkoutSessionDTO createSession(WorkoutSessionDTO dto) {
        WorkoutSessionModel session = new WorkoutSessionModel();
        session.setUserId(dto.getUserId());
        session.setObservation(dto.getObservation());
        session.setStartedAt(dto.getStartedAt());
        session.setEndedAt(dto.getEndedAt());

        if (dto.getTemplateId() != null) {
            WorkoutTemplateModel template = workoutTemplateRepository.findById(dto.getTemplateId())
                    .orElseThrow(() -> new IllegalArgumentException("Template not found"));
            session.setTemplate(template);
        }

        WorkoutSessionModel saved = workoutSessionRepository.save(session);
        persistSets(saved, dto.getSets());
        return toDTO(saved);
    }

    @Transactional
    public WorkoutSessionDTO updateSession(UUID id, WorkoutSessionDTO dto) {
        WorkoutSessionModel session = workoutSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        session.setObservation(dto.getObservation());
        session.setStartedAt(dto.getStartedAt());
        session.setEndedAt(dto.getEndedAt());

        if (dto.getTemplateId() != null) {
            WorkoutTemplateModel template = workoutTemplateRepository.findById(dto.getTemplateId())
                    .orElseThrow(() -> new IllegalArgumentException("Template not found"));
            session.setTemplate(template);
        } else {
            session.setTemplate(null);
        }

        workoutSetRepository.deleteAll(session.getSets());
        session.getSets().clear();
        persistSets(session, dto.getSets());
        return toDTO(workoutSessionRepository.save(session));
    }

    @Transactional
    public void deleteSession(UUID id) {
        if (!workoutSessionRepository.existsById(id)) {
            throw new IllegalArgumentException("Session not found");
        }
        workoutSessionRepository.deleteById(id);
    }

    private void persistSets(WorkoutSessionModel session, List<WorkoutSetDTO> sets) {
        if (sets == null) {
            return;
        }
        for (WorkoutSetDTO setDTO : sets) {
            WorkoutSetModel set = new WorkoutSetModel();
            set.setSession(session);
            set.setSetOrder(setDTO.getSetOrder());
            set.setReps(setDTO.getReps());
            set.setWeightKg(setDTO.getWeightKg());
            set.setRpe(setDTO.getRpe());
            set.setRestSeconds(setDTO.getRestSeconds());
            set.setWarmup(Boolean.TRUE.equals(setDTO.getWarmup()));
            set.setPerformedAt(setDTO.getPerformedAt());

            WorkoutExerciseModel exercise = workoutExerciseRepository.findById(setDTO.getExerciseId())
                    .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
            set.setExercise(exercise);

            session.addSet(set);
        }
    }

    private WorkoutSessionDTO toDTO(WorkoutSessionModel model) {
        return WorkoutSessionDTO.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .templateId(model.getTemplate() != null ? model.getTemplate().getId() : null)
                .startedAt(model.getStartedAt())
                .endedAt(model.getEndedAt())
                .observation(model.getObservation())
                .sets(model.getSets().stream()
                        .map(this::toSetDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private WorkoutSetDTO toSetDTO(WorkoutSetModel model) {
        return WorkoutSetDTO.builder()
                .id(model.getId())
                .exerciseId(model.getExercise().getId())
                .setOrder(model.getSetOrder())
                .reps(model.getReps())
                .weightKg(model.getWeightKg())
                .rpe(model.getRpe())
                .restSeconds(model.getRestSeconds())
                .warmup(model.getWarmup())
                .performedAt(model.getPerformedAt())
                .build();
    }
}
