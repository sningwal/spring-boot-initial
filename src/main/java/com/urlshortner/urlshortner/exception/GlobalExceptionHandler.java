package com.urlshortner.urlshortner.exception;

import com.urlshortner.urlshortner.dtos.response.ApiResponse;
import com.urlshortner.urlshortner.dtos.response.ResponseUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<?> handleAuthorizationException(UnAuthorizedException ex) {
        ApiResponse<?> response = ResponseUtil.error(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null,
                null
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(
            ResourceNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.error(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        null,
                        null
                ));
    }
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleAlreadyExists(
            ResourceAlreadyExistsException ex
    ) {

        ApiResponse<?> response = ResponseUtil.error(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof DateTimeParseException) {

            return ResponseEntity.badRequest().body(
                    ResponseUtil.error(
                            HttpStatus.BAD_REQUEST.value(),
                            "Invalid expiresAt value. Expected format: yyyy-MM-dd'T'HH:mm:ss'Z'",
                            null,
                            null
                    )
            );
        }

        return ResponseEntity.badRequest().body(
                ResponseUtil.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Malformed JSON request",
                        null,
                        null
                )
        );
    }

    // dto validations - method arguments validation - at fields level
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
        ApiResponse<?> response = ResponseUtil.error(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    @ExceptionHandler(QrCodeGenerationException.class)
    public ResponseEntity<ApiResponse<?>> handleQrCodeException(
            QrCodeGenerationException ex
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Unable to generate QR code",
                        null,
                        null
                ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        ApiResponse<?> response = ResponseUtil.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null,
                null
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
