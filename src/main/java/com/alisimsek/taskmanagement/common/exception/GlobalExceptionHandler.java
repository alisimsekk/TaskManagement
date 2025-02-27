package com.alisimsek.taskmanagement.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageBundleService messageBundleService;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.error(ex.getMessage(), ex);
        String localizedMessage = getLocalizedMessage(ex);
        ErrorResponse errorResponse = getErrorResponse(ex.getHttpStatus(), localizedMessage, ex.getErrorCode(), null);
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ErrorResponse errorResponse = getErrorResponse(HttpStatus.BAD_REQUEST, null, null, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private String getLocalizedMessage(BaseException ex) {
        return messageBundleService.getMessage("exception.message.".concat(ex.getMessageKey()), ex.getArgs());
    }

    private static ErrorResponse getErrorResponse(HttpStatus httpStatus, String message, String errorCode, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus.value())
                .message(message)
                .errorCode(errorCode)
                .validationErrors(fieldErrors)
                .build();
    }
}
