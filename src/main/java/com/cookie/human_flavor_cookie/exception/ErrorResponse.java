package com.cookie.human_flavor_cookie.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private String message;
    private HttpStatus status;

    public ErrorResponse(){}
    public ErrorResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    // Getter, Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
