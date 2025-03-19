package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidUserTypeException extends BaseException {
    public InvalidUserTypeException() {
        super(HttpStatus.BAD_REQUEST, "4013");
    }
}
