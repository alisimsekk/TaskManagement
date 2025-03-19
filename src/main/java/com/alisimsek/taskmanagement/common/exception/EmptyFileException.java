package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class EmptyFileException extends BaseException {
    public EmptyFileException() {
        super(HttpStatus.BAD_REQUEST, "4003");
    }
}
