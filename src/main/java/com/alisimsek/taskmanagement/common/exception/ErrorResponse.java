package com.alisimsek.taskmanagement.common.exception;

import com.alisimsek.taskmanagement.common.response.ApiResponseStatus;
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
    @Builder.Default
    private ApiResponseStatus status = ApiResponseStatus.ERROR;
    private int httpStatus;
    private String message;
    private String errorCode;
    private Map<String, String> validationErrors;
}
