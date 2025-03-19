package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class FileStorageException extends BaseException{
    public FileStorageException() {
        super(HttpStatus.BAD_REQUEST, "4008");
    }
}
