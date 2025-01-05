package com.cookie.human_flavor_cookie.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 회원 관련
    EMAIL_ALREADY_EXISTS("ALREADY EXIST.", HttpStatus.CONFLICT),
    PASSWORD_INCORRECT("PASSWORD INCORRECT.", HttpStatus.UNAUTHORIZED),
    NO_MEMBER("NO EXIST MEMBER.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("NEED LOGIN", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}