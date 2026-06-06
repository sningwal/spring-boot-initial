package com.urlshortner.urlshortner.controller;

import com.urlshortner.urlshortner.dtos.request.CreateShortUrlRequest;
import com.urlshortner.urlshortner.dtos.response.*;
import com.urlshortner.urlshortner.entities.ShortUrl;
import com.urlshortner.urlshortner.entities.User;
import com.urlshortner.urlshortner.service.ShortUrlService;
import com.urlshortner.urlshortner.util.AppConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/short-urls")
public class URLController {
    @Autowired
    ShortUrlService shortUrlService;
//    @GetMapping
//    public ResponseEntity<?> getPublicUrls(Authentication authentication){
//        return ResponseEntity.ok(shortUrlService.findPublicShortUrls());
//    }
    @PostMapping
    public ResponseEntity<?> generateShortUrl(@Valid @RequestBody CreateShortUrlRequest request, Authentication authentication) throws Exception {
        ShortUrlResponse shortUrlResponse = shortUrlService.generateShortUrl(request, authentication);
        ApiResponse<?> apiResponse =  ResponseUtil.success(HttpStatus.OK.value(),"short url created successfully.",shortUrlResponse,null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
@PostMapping("/{shortKey}")
public ResponseEntity<?> redirectToOriginalUrl(
        @PathVariable String shortKey) {
    System.out.println(shortUrlService.getOriginalUrl(shortKey));
     String originalUrl = shortUrlService.getOriginalUrl(shortKey);
    return ResponseEntity.ok(Map.of("original_url", originalUrl));
}
    @GetMapping
    public ResponseEntity<?> getUserShortUrls( @RequestParam(value = "page", defaultValue = "0") int page,
                                               @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
                                               @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                               @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                               Authentication authentication){

        if(authentication.isAuthenticated()){
            User user = (User) authentication.getPrincipal();
            System.out.println(user.getEmail());
            Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            var pageable = PageRequest.of(page, size); // page, size, sort
            Page<ShortUrlDto> shortUrlPage =  shortUrlService.findByAllUser(user,pageable);
            ApiResponse<?> apiResponse =  ResponseUtil.success(HttpStatus.OK.value(),"records fetched successfully.",shortUrlPage,null);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        AuthResponse authResponse = AuthResponse.builder()
                .message("User Not Authenticated!")
                .success(false)
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
    }
}
