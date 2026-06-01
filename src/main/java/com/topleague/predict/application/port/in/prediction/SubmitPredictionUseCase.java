package com.topleague.predict.application.port.in.prediction;

import com.topleague.predict.domain.model.Prediction;

public interface SubmitPredictionUseCase {
    Prediction submitPrediction(Prediction prediction);
}
