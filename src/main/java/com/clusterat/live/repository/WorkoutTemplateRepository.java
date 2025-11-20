package com.clusterat.live.repository;

import com.clusterat.live.model.WorkoutTemplateModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplateModel, Long> {
    List<WorkoutTemplateModel> findByCreatedByUserId(Long userId);

    @Override
    @EntityGraph(attributePaths = {"exercises", "exercises.exercise"})
    List<WorkoutTemplateModel> findAll();
}
