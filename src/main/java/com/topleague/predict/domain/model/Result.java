package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Result {
    private final Integer gameId;
    private final Integer homeScore;
    private final Integer awayScore;
}
