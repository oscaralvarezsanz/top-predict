package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GroupMemberPrediction {
    private final Integer userId;
    private final String alias;
    private final Integer predictedHomeScore;
    private final Integer predictedAwayScore;
    private final Integer pointsEarned;
}
