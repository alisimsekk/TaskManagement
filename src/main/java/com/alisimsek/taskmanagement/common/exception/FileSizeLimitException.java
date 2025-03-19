package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class FileSizeLimitException extends BaseException {
    public FileSizeLimitException() {
        super(HttpStatus.BAD_REQUEST, "4005");
    }
}
