package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class CompletedTaskModificationException extends BaseException {
    public CompletedTaskModificationException() {
        super(HttpStatus.BAD_REQUEST, "4010");
    }
}
