package com.topleague.predict.application.port.out.game;

import com.topleague.predict.domain.model.Game;
import java.util.List;

public interface GameGetByLeagueAndMatchdayRepository {
    List<Game> getGamesByLeagueAndMatchday(Integer leagueId, Integer matchday);
}
