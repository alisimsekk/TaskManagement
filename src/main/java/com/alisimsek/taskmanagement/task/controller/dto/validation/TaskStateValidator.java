package com.alisimsek.taskmanagement.task.controller.dto.validation;

import com.alisimsek.taskmanagement.task.controller.dto.request.TaskStateUpdateRequest;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class TaskStateValidator implements ConstraintValidator<ValidTaskStateAndReason, TaskStateUpdateRequest> {

    @Override
    public boolean isValid(TaskStateUpdateRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (TaskState.CANCELLED.equals(request.getState()) && (Objects.isNull(request.getCancellationReason()) || request.getCancellationReason().isBlank())) {
            context.buildConstraintViolationWithTemplate("Cancellation reason must be provided when the state is CANCELLED.")
                    .addPropertyNode("cancellationReason")
                    .addConstraintViolation();
            isValid = false;
        }

        if (TaskState.BLOCKED.equals(request.getState()) && (Objects.isNull(request.getBlockReason())  || request.getBlockReason().isBlank())) {
            context.buildConstraintViolationWithTemplate("Block reason must be provided when the state is BLOCKED.")
                    .addPropertyNode("blockReason")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
