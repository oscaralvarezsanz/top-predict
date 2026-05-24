package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Prediction {
    private final Integer id;
    private final Integer groupId;
    private final Integer userId;
    private final Integer gameId;
    private final Integer predictedHomeScore;
    private final Integer predictedAwayScore;
    private final Integer pointsEarned;
    @Builder.Default
    private final PredictionStatus status = PredictionStatus.PENDING;
}
