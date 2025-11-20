package com.clusterat.live.repository;

import com.clusterat.live.model.WorkoutSessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSessionModel, UUID> {
    List<WorkoutSessionModel> findByUserIdOrderByStartedAtDesc(Long userId);
    List<WorkoutSessionModel> findByUserIdAndStartedAtBetween(Long userId, OffsetDateTime start, OffsetDateTime end);
}

