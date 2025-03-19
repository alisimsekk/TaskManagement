package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedDirectoryAccessException extends BaseException {
    public UnauthorizedDirectoryAccessException() {
        super(HttpStatus.UNAUTHORIZED, "4002");
    }
}
