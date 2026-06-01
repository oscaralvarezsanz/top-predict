package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Game {
    private final Integer id;
    private final Integer leagueId;
    private final Integer homeTeamId;
    private final Integer awayTeamId;
    private final Integer homeScore;
    private final Integer awayScore;
    private final LocalDate date;
    private final Integer matchday;
}
