package com.topleague.predict.application.port.out.prediction;

import com.topleague.predict.domain.model.Prediction;
import java.util.Optional;

public interface PredictionGetByGroupUserAndGameRepository {
    Optional<Prediction> getPredictionByGroupUserAndGame(Integer groupId, Integer userId, Integer gameId);
}
