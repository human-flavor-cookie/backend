package com.cookie.human_flavor_cookie.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 회원 관련
    EMAIL_ALREADY_EXISTS("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    PASSWORD_INCORRECT("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    NO_MEMBER("존재하는 회원이 없습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);

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