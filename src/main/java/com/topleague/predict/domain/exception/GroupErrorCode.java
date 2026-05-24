package com.topleague.predict.domain.exception;

import lombok.Getter;

@Getter
public enum GroupErrorCode implements TopPredictErrorCode {
    INVALID_GROUP_DATA("GRP-001", "Invalid group data"),
    GROUP_EXISTS("GRP-002", "Group already exists"),
    ERROR_SAVING_GROUP("GRP-003", "Error while saving group"),
    GROUP_NOT_FOUND("GRP-004", "Group does not exist"),
    COULD_NOT_GENERATE_INVITE_CODE("GRP-005", "Could not generate a unique invite code");

    private final String code;
    private final String message;

    GroupErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
