package com.topleague.predict.application.port.in.prediction;

import com.topleague.predict.domain.model.Prediction;

import java.util.List;

public interface GetMyGroupPredictionsUseCase {
    List<Prediction> getMyGroupPredictions(Integer groupId, Integer userId);
}
