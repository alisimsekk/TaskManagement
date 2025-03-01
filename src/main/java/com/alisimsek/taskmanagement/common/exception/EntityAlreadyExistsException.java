package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "4001");
    }
}
