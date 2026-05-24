package com.topleague.predict.application.service.scoring;

import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.domain.model.Result;

public interface PredictionScoringStrategy {
    int calculatePoints(Prediction prediction, Result result);
}
