package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class FileTypeNotAllowedException extends BaseException{
    public FileTypeNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, "4006");
    }
}
