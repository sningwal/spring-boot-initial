package com.urlshortner.urlshortner.dtos.request;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateShortUrlRequest(

        @NotBlank(message = "Original URL is required")
        @Pattern(
                regexp = "^(https?|ftp)://.*$",
                message = "Invalid URL format"
        )
        String originalUrl,
        Boolean isPrivate,
        @Size(min = 4, max = 20,
                message = "Custom alias must be between 4 and 20 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9_-]+$",
                message = "Alias can contain only letters, numbers, underscore and hyphen"
        )
        String customAlias,
        @Future(message = "Expiration date must be in the future")
        Instant expiresAt,
        boolean generateQrCode
) {
}