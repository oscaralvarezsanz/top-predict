package com.topleague.predict.infrastructure.out.rest.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebGameResponse {
    private Integer id;
    private Integer leagueId;
    private Integer awayTeamId;
    private Integer homeTeamId;
    private Integer awayScore;
    private Integer homeScore;
    private LocalDate date;
    private Integer matchday;
}
