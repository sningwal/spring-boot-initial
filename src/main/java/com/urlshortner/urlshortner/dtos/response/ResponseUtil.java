package com.urlshortner.urlshortner.dtos.response;

import java.time.Instant;

public class ResponseUtil {
    private ResponseUtil() {
        // prevent instantiation
    }
    public static <T> ApiResponse<T> success(
            Integer statusCode,
            String message,
            T data,
            Object metadata
    ) {
        return ApiResponse.<T>builder()
                .status("success")
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<?> error(
            Integer statusCode,
            String message,
            Object errors,
            Object metadata
    ) {
        return ApiResponse.builder()
                .status("error")
                .statusCode(statusCode)
                .message(message)
                .errors(errors)
                .metadata(metadata)
                .timestamp(Instant.now())
                .build();
    }
}

