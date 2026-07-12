package com.example.demo.util;

import com.example.demo.dto.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PaginationUtil {

    public static <T, R> PagedResponse<R> buildResponse(Page<T> page, Function<T, R> mapper) {

        return PagedResponse.<R>builder().content(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}