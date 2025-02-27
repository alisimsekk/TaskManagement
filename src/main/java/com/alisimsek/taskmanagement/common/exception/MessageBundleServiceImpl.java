package com.alisimsek.taskmanagement.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageBundleServiceImpl implements MessageBundleService {

    private final MessageSource messageSource;

    @Override
    public String getMessage(String code, Object... args) {
        return getMessage(code, LocaleContextHolder.getLocale(), args);
    }

    @Override
    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(String code, String defaultMessage, Object... args) {
        return getMessage(code, LocaleContextHolder.getLocale(), defaultMessage, args);
    }

    @Override
    public String getMessage(String code, Locale locale, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
}
