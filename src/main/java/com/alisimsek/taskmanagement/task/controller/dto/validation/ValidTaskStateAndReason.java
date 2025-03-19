package com.alisimsek.taskmanagement.task.controller.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskStateValidator.class)
@Documented
public @interface ValidTaskStateAndReason {
    String message() default "Cancellation or blocked reason cannot be blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
