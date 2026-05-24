package com.topleague.predict.domain.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TopPredictException extends RuntimeException {
    private final TopPredictErrorCode errorCode;

    public TopPredictException(TopPredictErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TopPredictException(TopPredictErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
