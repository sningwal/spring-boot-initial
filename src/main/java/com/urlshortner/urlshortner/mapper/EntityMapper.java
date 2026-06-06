package com.urlshortner.urlshortner.mapper;

import com.urlshortner.urlshortner.dtos.response.ShortUrlDto;
import com.urlshortner.urlshortner.dtos.response.UserDto;
import com.urlshortner.urlshortner.entities.ShortUrl;
import com.urlshortner.urlshortner.entities.User;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EntityMapper {
    public ShortUrlDto toShortUrlDto(ShortUrl shortUrl) {
        UserDto userDto = null;
        if(shortUrl.getCreatedBy() != null) {
            userDto = toUserDto(shortUrl.getCreatedBy());
        }
        return new ShortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                userDto,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );
    }

//    Long id, String shortKey, String shortUrl, String originalUrl,
//    Boolean isPrivate, Instant expiresAt,
//    UserDto createdBy, Long clickCount,
//    Instant createdAt
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName());
    }
}