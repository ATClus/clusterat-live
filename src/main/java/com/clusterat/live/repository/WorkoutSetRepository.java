package com.clusterat.live.repository;

import com.clusterat.live.model.WorkoutSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSetModel, Long> {
    List<WorkoutSetModel> findBySessionIdOrderBySetOrderAsc(UUID sessionId);
}

