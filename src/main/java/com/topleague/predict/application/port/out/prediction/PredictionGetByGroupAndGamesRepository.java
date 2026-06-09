package com.topleague.predict.application.port.out.prediction;

import com.topleague.predict.domain.model.Prediction;
import java.util.List;

public interface PredictionGetByGroupAndGamesRepository {
    List<Prediction> getPredictionsByGroupAndGames(Integer groupId, List<Integer> gameIds);
}
