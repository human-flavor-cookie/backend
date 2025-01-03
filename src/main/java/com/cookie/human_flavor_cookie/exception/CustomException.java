package com.cookie.human_flavor_cookie.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}