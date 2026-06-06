package com.urlshortner.urlshortner.dtos.response;

import java.time.Instant;

public record ShortUrlResponse(
        Long id,
        String shortKey,
        String shortUrl,
        String qrCodeGenerated,
        String originalUrl,
        Boolean isPrivate,
        Instant expiresAt,
        UserDto createdBy,
        Long clickCount,
        Instant createdAt
) {}