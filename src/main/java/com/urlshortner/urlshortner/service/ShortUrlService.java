package com.urlshortner.urlshortner.service;

import com.urlshortner.urlshortner.config.ApplicationProperties;
import com.urlshortner.urlshortner.dtos.request.CreateShortUrlRequest;
import com.urlshortner.urlshortner.dtos.response.ShortUrlDto;
import com.urlshortner.urlshortner.dtos.response.ShortUrlResponse;
import com.urlshortner.urlshortner.dtos.response.UrlRedirectView;
import com.urlshortner.urlshortner.entities.ShortUrl;
import com.urlshortner.urlshortner.entities.User;
import com.urlshortner.urlshortner.exception.ResourceAlreadyExistsException;
import com.urlshortner.urlshortner.exception.ResourceNotFoundException;
import com.urlshortner.urlshortner.exception.UnAuthorizedException;
import com.urlshortner.urlshortner.mapper.EntityMapper;
import com.urlshortner.urlshortner.repository.ShortUrlRepository;
import com.urlshortner.urlshortner.util.ShortCodeGenerator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Base64;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class ShortUrlService {

    private final ApplicationProperties appProperties;
    private final ShortUrlRepository shortUrlRepository;
    private final EntityMapper entityMapper;
    private final ShortCodeGenerator shortCodeGenerator;
    private static final int MAX_COLLISION_RETRIES = 5;

   private final QrCodeService qrCodeService;
   private final ClickCountService clickCountService;

    public ShortUrlService(QrCodeService qrCodeService, ApplicationProperties appProperties, ShortUrlRepository shortUrlRepository, EntityMapper entityMapper, ShortCodeGenerator shortCodeGenerator, ClickCountService clickCountService) {
        this.qrCodeService = qrCodeService;
        this.appProperties = appProperties;
        this.shortUrlRepository = shortUrlRepository;
        this.entityMapper = entityMapper;
        this.shortCodeGenerator = shortCodeGenerator;
        this.clickCountService = clickCountService;
    }

    public List<ShortUrlDto> findPublicShortUrls(){
       return shortUrlRepository.findPublicShortUrls()
               .stream().map(entityMapper::toShortUrlDto).toList();
   }
    /**
     * Generates a unique short code by handling potential collisions.
     * @return A unique short code that doesn't exist in the database
     */
    private String generateUniqueShortCode() {
        int attempts = 0;
        String shortKey;
        do {
            shortKey = shortCodeGenerator.generate();
            attempts++;

            if (attempts > MAX_COLLISION_RETRIES) {
                throw new RuntimeException("Failed to generate unique short code after " +
                        MAX_COLLISION_RETRIES + " attempts");
            }
        } while (shortUrlRepository.findByShortKey(shortKey).isPresent());

        return shortKey;
    }

    /**
     * Creates a short URL for the given original URL.
     * If the URL already exists, returns the existing ShortUrl.
     * Otherwise, generates a unique short code and saves it to the database.
     * @param /originalUrl The original URL to shorten
     * @return The ShortUrl entity (existing or newly created)
     */
    public ShortUrlResponse generateShortUrl(@Valid CreateShortUrlRequest request, Authentication authentication) {

        if(shortUrlRepository.existsByShortKey(request.customAlias())){
            throw new ResourceAlreadyExistsException("custom url already exist!.");
        }
        Instant customExpiredAt = request.expiresAt() != null? request.expiresAt():Instant.now().plus(30, DAYS);
        var shortKey = request.customAlias() != null? request.customAlias():generateUniqueShortCode();

        byte[] qrCodeGenerated = request.generateQrCode()?qrCodeService.generateQrCode( appProperties.baseUrl()
                + "/"
                + shortKey,300,300):new byte[0];;

        String qrCodeBase64 = Base64.getEncoder()
                .encodeToString(qrCodeGenerated);

        User user = authentication == null
                ? null
                : (User) authentication.getPrincipal();
        System.out.println(user);
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(request.originalUrl());
        shortUrl.setShortKey(shortKey);
        shortUrl.setCreatedBy(null);
        shortUrl.setIsPrivate(request.isPrivate());
        shortUrl.setClickCount(0L);
        shortUrl.setExpiresAt(customExpiredAt); // default & custom
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setCreatedBy(user);
        shortUrlRepository.save(shortUrl);
        ShortUrlDto shortUrlDto = entityMapper.toShortUrlDto(shortUrl);

        return new ShortUrlResponse(
                shortUrlDto.id(),
                shortUrlDto.shortKey(),
                appProperties.baseUrl() + "/" + shortUrlDto.shortKey(),
                qrCodeBase64,
                shortUrlDto.originalUrl(),
                shortUrlDto.isPrivate(),
                shortUrlDto.expiresAt(),
                shortUrlDto.createdBy(),
                shortUrlDto.clickCount(),
                shortUrlDto.createdAt()
        );
    }

    /**
     * Retrieves the original URL for a given short code.
     *
     * @param /shortKey The short code to lookup
     * @return Optional containing the original URL if found, empty otherwise
     */
//    @Async
//    private void updateClickCount(Long shortKey){
//        shortUrlRepository.updateClickCount(shortKey);
//    }
    @Transactional
    public String getOriginalUrl(String shortKey) {
        UrlRedirectView shortUrl = shortUrlRepository.findByShortKey(shortKey)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));
        if (shortUrl.getIsPrivate()) {
            throw new UnAuthorizedException("please login to access url.");
        }
        clickCountService.updateClickCount(shortUrl.getId());
        return shortUrl.getOriginalUrl();
    }

    public Page<ShortUrlDto> findByAllUser(User user, Pageable pageable) {
        Page<ShortUrl> shortUrls = shortUrlRepository.findByCreatedBy(user.getId(), pageable);
        return shortUrls.map(entityMapper::toShortUrlDto);
    }
}
