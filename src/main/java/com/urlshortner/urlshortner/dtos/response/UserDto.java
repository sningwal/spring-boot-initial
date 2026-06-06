package com.urlshortner.urlshortner.dtos.response;

import java.io.Serializable;

public record UserDto(Long id, String name) implements Serializable {
}