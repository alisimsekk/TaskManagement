package com.alisimsek.taskmanagement.common.exception;

import java.util.Locale;

public interface MessageBundleService {

    String getMessage(String code, Object... args);

    String getMessage(String code, Locale locale, Object... args);

    String getMessage(String code, String defaultMessage, Object... args);

    String getMessage(String code, Locale locale, String defaultMessage, Object... args);
}
