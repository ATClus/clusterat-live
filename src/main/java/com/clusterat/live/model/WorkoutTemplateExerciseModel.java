package com.clusterat.live.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exercises_templates", schema = "live")
public class WorkoutTemplateExerciseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    @ToString.Exclude
    private WorkoutTemplateModel template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private WorkoutExerciseModel exercise;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "target_sets")
    private Integer targetSets;

    @Column(name = "target_reps", length = 20)
    private String targetReps;

    @Column(name = "target_rest_sec")
    private Integer targetRestSec;

    @PrePersist
    void onCreate() {
        if (targetSets == null) {
            targetSets = 4;
        }
        if (targetReps == null) {
            targetReps = "8-12";
        }
        if (targetRestSec == null) {
            targetRestSec = 60;
        }
    }
}
