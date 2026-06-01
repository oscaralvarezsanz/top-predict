package com.topleague.predict.application.port.out.prediction;

import com.topleague.predict.domain.model.Prediction;

public interface PredictionSaveRepository {
    Prediction savePrediction(Prediction prediction);
}
