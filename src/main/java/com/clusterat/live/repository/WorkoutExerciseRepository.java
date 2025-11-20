package com.clusterat.live.repository;

import com.clusterat.live.model.WorkoutExerciseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExerciseModel, Long> {
    List<WorkoutExerciseModel> findByMuscleGroupOrderByNameAsc(String muscleGroup);
}

