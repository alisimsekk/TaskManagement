package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class FileNotFoundException extends BaseException{
    public FileNotFoundException() {
        super(HttpStatus.NOT_FOUND, "4009");
    }
}
