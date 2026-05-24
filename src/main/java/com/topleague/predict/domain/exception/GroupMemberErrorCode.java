package com.topleague.predict.domain.exception;

import lombok.Getter;

@Getter
public enum GroupMemberErrorCode implements TopPredictErrorCode {
    INVALID_MEMBER_DATA("MBR-001", "Invalid group member data"),
    MEMBER_EXISTS("MBR-002", "Member already exists in this group"),
    ERROR_SAVING_MEMBER("MBR-003", "Error while saving group member"),
    MEMBER_NOT_FOUND("MBR-004", "Member does not exist");

    private final String code;
    private final String message;

    GroupMemberErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
