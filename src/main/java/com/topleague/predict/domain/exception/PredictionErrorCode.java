package com.topleague.predict.domain.exception;

import lombok.Getter;

@Getter
public enum PredictionErrorCode implements TopPredictErrorCode {
    INVALID_PREDICTION_DATA("PRED-001", "Invalid prediction data"),
    PREDICTION_EXISTS("PRED-002", "Prediction already exists"),
    PREDICTION_NOT_FOUND("PRED-003", "Prediction does not exist"),
    MISMATCHED_GAME_IDS("PRED-004", "Prediction and result gameId must match"),
    PREDICTION_LOCKED("PRED-005", "Prediction is locked");

    private final String code;
    private final String message;

    PredictionErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
