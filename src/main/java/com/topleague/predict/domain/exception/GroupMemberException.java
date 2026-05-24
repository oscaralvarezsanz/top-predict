package com.topleague.predict.domain.exception;

public class GroupMemberException extends TopPredictException {

    public GroupMemberException(GroupMemberErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public GroupMemberException(GroupMemberErrorCode errorCode) {
        super(errorCode);
    }
}
