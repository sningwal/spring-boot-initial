package com.urlshortner.urlshortner.dtos.request;

import com.urlshortner.urlshortner.entities.Role;

public record CreateUserRequest(String email,
                                String password,
                                String name,
                                Role role) {
}


