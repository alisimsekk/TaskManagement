package com.alisimsek.taskmanagement.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpHeaders;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {

    public static <T> HttpHeaders generatePaginationHeaders(Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(page.getTotalPages()));
        headers.add("X-Current-Page", String.valueOf(page.getNumber()));
        headers.add("X-Page-Size", String.valueOf(page.getSize()));
        headers.add("X-Has-Next", String.valueOf(page.hasNext()));
        headers.add("X-Has-Previous", String.valueOf(page.hasPrevious()));
        return headers;
    }
}
