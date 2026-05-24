package com.topleague.predict.domain.exception;

public class GroupException extends TopPredictException {

    public GroupException(GroupErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public GroupException(GroupErrorCode errorCode) {
        super(errorCode);
    }
}
