package com.alisimsek.taskmanagement.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int httpStatus;
    private String message;
    private String errorCode;
    private Map<String, String> validationErrors;
}
