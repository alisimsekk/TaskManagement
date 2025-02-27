package com.alisimsek.taskmanagement.common.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseException{

    public EntityNotFoundException(Object[] args) {
        super(args[0].toString().concat(" not found"), HttpStatus.NOT_FOUND, "4000", args);
    }
}
