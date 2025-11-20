package com.clusterat.live.repository;

import com.clusterat.live.model.WorkoutTemplateExerciseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutTemplateExerciseRepository extends JpaRepository<WorkoutTemplateExerciseModel, Long> {
    List<WorkoutTemplateExerciseModel> findByTemplateIdOrderByDisplayOrderAsc(Long templateId);
}

