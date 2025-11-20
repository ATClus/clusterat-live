package com.clusterat.live.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workout_templates", schema = "live")
public class WorkoutTemplateModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    @ToString.Exclude
    private List<WorkoutTemplateExerciseModel> exercises = new ArrayList<>();

    public void addExercise(WorkoutTemplateExerciseModel exercise) {
        exercise.setTemplate(this);
        exercises.add(exercise);
    }
}
