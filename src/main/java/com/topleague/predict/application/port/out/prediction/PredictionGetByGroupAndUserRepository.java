package com.topleague.predict.application.port.out.prediction;

import com.topleague.predict.domain.model.Prediction;

import java.util.List;

public interface PredictionGetByGroupAndUserRepository {
    List<Prediction> getPredictionsByGroupAndUser(Integer groupId, Integer userId);
}
