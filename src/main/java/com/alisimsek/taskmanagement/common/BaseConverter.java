package com.alisimsek.taskmanagement.common;

import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseConverter<S, T> implements Converter<S, T> {

    public List<T> convertList(List<S> sourceList) {
        return sourceList.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
