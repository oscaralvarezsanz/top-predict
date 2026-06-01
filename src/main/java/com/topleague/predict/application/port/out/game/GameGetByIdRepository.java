package com.topleague.predict.application.port.out.game;

import com.topleague.predict.domain.model.Game;
import java.util.Optional;

public interface GameGetByIdRepository {
    Optional<Game> getGameById(Integer gameId);
}
