package com.topleague.predict.application.port.in.prediction;

import com.topleague.predict.domain.model.Result;

public interface ResolveGamePredictionsUseCase {
    void resolvePredictions(Result result);
}
