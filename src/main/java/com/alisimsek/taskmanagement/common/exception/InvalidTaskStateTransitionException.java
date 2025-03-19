package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidTaskStateTransitionException extends BaseException{
    public InvalidTaskStateTransitionException() {
        super(HttpStatus.BAD_REQUEST, "4011");
    }
}
