package com.alisimsek.taskmanagement.common.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodeLookUp {

    private ErrorCodeLookUp() {}

    private static final Map<String, String> value;

    static {
        value = new HashMap<>();
        value.put("4000", "ENTITY_NOT_FOUND_EXCEPTION");
        value.put("4001", "ENTITY_ALREADY_EXISTS_EXCEPTION");
        value.put("4002", "UNAUTHORIZED_DIRECTORY_ACCESS_EXCEPTION");
        value.put("4003", "EMPTY_FILE_EXCEPTION");
        value.put("4004", "INVALID_FILE_NAME_EXCEPTION");
        value.put("4005", "FILE_SIZE_LIMIT_EXCEPTION");
        value.put("4006", "FILE_TYPE_NOT_ALLOWED_EXCEPTION");
        value.put("4007", "FILE_EXTENSION_NOT_FOUND_EXCEPTION");
        value.put("4008", "FILE_STORAGE_EXCEPTION");
        value.put("4009", "FILE_NOT_FOUND_EXCEPTION");
        value.put("4010", "COMPLETED_TASK_MODIFICATION_EXCEPTION");
        value.put("4011", "INVALID_TASK_STATE_TRANSITION_EXCEPTION");
        value.put("4012", "ACCESS_DENIED_EXCEPTION");
        value.put("4013", "INVALID_USER_TYPE_EXCEPTION");
    }

    public static String getMessageKey(String errorCode) {
        return value.getOrDefault(errorCode, "GENERIC_EXCEPTION");
    }
}
