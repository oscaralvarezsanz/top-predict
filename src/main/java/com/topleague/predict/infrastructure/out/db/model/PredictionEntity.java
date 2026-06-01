package com.topleague.predict.infrastructure.out.db.model;

import com.topleague.predict.domain.model.PredictionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "prediction",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"group_id", "user_id", "game_id"})}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(name = "predicted_home_score", nullable = false)
    private Integer predictedHomeScore;

    @Column(name = "predicted_away_score", nullable = false)
    private Integer predictedAwayScore;

    @Column(name = "points_earned")
    private Integer pointsEarned;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PredictionStatus status;
}
