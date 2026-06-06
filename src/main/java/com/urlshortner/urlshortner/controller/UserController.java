package com.urlshortner.urlshortner.controller;

import com.urlshortner.urlshortner.dtos.request.CreateUserRequest;
import com.urlshortner.urlshortner.dtos.request.RegisterUserRequest;
import com.urlshortner.urlshortner.dtos.request.SignInRequest;
import com.urlshortner.urlshortner.dtos.response.ApiResponse;
import com.urlshortner.urlshortner.dtos.response.AuthResponse;
import com.urlshortner.urlshortner.dtos.response.ResponseUtil;
import com.urlshortner.urlshortner.entities.Role;
import com.urlshortner.urlshortner.entities.User;
import com.urlshortner.urlshortner.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ApiResponse<AuthResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerRequest) {
            var cmd = new CreateUserRequest(
                    registerRequest.email(),
                    registerRequest
                            .password(),
                    registerRequest.name(),
                    Role.ROLE_USER
            );
            userService.createUser(cmd);
        AuthResponse authResponse = AuthResponse.builder()
                .name(cmd.name())
                .email(cmd.email())
                .accessToken("")
//                    .refreshToken("refreshToken")
//                    .isVerified(Boolean.TRUE)
                .success(Boolean.TRUE)
                .role(Role.ROLE_USER)
                .message("Sign in successful")
                .build();
        return ResponseUtil.success(HttpStatus.OK.value(),"Sign in successfully",authResponse,null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }
}
