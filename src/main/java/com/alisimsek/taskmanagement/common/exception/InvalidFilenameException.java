package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidFilenameException extends BaseException {
    public InvalidFilenameException() {
        super(HttpStatus.BAD_REQUEST, "4004");
    }
}
