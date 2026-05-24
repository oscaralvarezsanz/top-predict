package com.topleague.predict.domain.exception;

public class PredictionException extends TopPredictException {

    public PredictionException(PredictionErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public PredictionException(PredictionErrorCode errorCode) {
        super(errorCode);
    }
}
