package com.topleague.predict.application.port.out.prediction;

import com.topleague.predict.domain.model.Prediction;

import java.util.List;

public interface PredictionGetByGroupAndGameRepository {
    List<Prediction> getPredictionsByGroupAndGame(Integer groupId, Integer gameId);
}
