package com.urlshortner.urlshortner.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortner.urlshortner.dtos.response.ApiResponse;
import com.urlshortner.urlshortner.dtos.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ApiResponse<?> apiResponse = ResponseUtil.error(HttpStatus.FORBIDDEN.value(),"You do not have permission to access this resource",null,null);

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}