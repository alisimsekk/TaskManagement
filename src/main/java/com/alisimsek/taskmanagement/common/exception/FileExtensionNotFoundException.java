package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class FileExtensionNotFoundException extends BaseException {
    public FileExtensionNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "4007");
    }
}
