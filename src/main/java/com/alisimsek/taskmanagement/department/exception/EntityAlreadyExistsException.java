package com.alisimsek.taskmanagement.department.exception;

import com.alisimsek.taskmanagement.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "4001");
    }
}
