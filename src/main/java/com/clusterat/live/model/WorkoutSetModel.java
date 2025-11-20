package com.clusterat.live.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workout_sets", schema = "live")
public class WorkoutSetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSessionModel session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private WorkoutExerciseModel exercise;

    @Column(name = "set_order", nullable = false)
    private Integer setOrder;

    @Column(name = "reps", nullable = false)
    private Integer reps;

    @Column(name = "weight_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "rpe")
    private Integer rpe;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(name = "is_warmup", nullable = false)
    private Boolean warmup;

    @Column(name = "performed_at", nullable = false)
    private OffsetDateTime performedAt;

    @PrePersist
    void onCreate() {
        if (performedAt == null) {
            performedAt = OffsetDateTime.now();
        }
        if (warmup == null) {
            warmup = false;
        }
    }
}
