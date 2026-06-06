package com.urlshortner.urlshortner.service;

import com.urlshortner.urlshortner.dtos.request.CreateUserRequest;
import com.urlshortner.urlshortner.dtos.request.SignInRequest;
import com.urlshortner.urlshortner.dtos.response.ApiResponse;
import com.urlshortner.urlshortner.dtos.response.AuthResponse;
import com.urlshortner.urlshortner.dtos.response.ResponseUtil;
import com.urlshortner.urlshortner.entities.User;
import com.urlshortner.urlshortner.exception.ResourceAlreadyExistsException;
import com.urlshortner.urlshortner.exception.ResourceNotFoundException;
import com.urlshortner.urlshortner.repository.UserRepository;
import com.urlshortner.urlshortner.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserService {
    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(AuthUtil authUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.authUtil = authUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        var user = new User();
        user.setEmail(createUserRequest.email());
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setName(createUserRequest.name());
        user.setRole(createUserRequest.role());
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
    }
    public ApiResponse<?> login(SignInRequest loginRequest) {
        System.out.println(loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException(loginRequest.getEmail()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                )
        );
        String token = authUtil.generateAccessToken(user);
        if(authentication.isAuthenticated()){
            AuthResponse authResponse =  AuthResponse.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .accessToken(token)
//                    .refreshToken("refreshToken")
//                    .isVerified(Boolean.TRUE)
                    .success(Boolean.TRUE)
//                    .role("ADMIN")
                    .message("Sign in successful")
                    .build();
//            Integer statusCode, String message, Object data, Object metadata
            return ResponseUtil.success(200,"Sign in successful", authResponse,null);
        }
        AuthResponse authResponse = AuthResponse.builder()
                .message("User Not Authenticated!")
                .success(false)
                .build();

        return ResponseUtil.error(401,"User Not Authenticated!",authResponse, null);
    }
}