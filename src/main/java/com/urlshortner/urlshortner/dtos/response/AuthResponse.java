package com.urlshortner.urlshortner.dtos.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.urlshortner.urlshortner.entities.Role;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;
    private Boolean isVerified;
    private Role role;
    private Boolean success;
    private String message;
}
