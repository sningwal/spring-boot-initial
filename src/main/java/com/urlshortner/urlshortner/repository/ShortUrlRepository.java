package com.urlshortner.urlshortner.repository;

import com.urlshortner.urlshortner.dtos.response.ShortUrlDto;
import com.urlshortner.urlshortner.dtos.response.UrlRedirectView;
import com.urlshortner.urlshortner.entities.ShortUrl;
import com.urlshortner.urlshortner.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl,Long> {
    @Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false order by su.createdAt desc")
    List<ShortUrl> findPublicShortUrls();
    boolean existsByShortKey(String shortKey);

    @Query("""
       select su.originalUrl as originalUrl,
              su.isPrivate as isPrivate,
              su.id as id
       from ShortUrl su
       where su.shortKey = :shortKey
       """)
    Optional<UrlRedirectView> findByShortKey(String shortKey);

    Optional<ShortUrl> findByOriginalUrl(@NotBlank(message = "Original URL is required") @Pattern(
                regexp = "^(https?|ftp)://.*$",
                message = "Invalid URL format"
        ) String s);
    @Modifying
    @Transactional
    @Query("update ShortUrl su set su.clickCount = su.clickCount + 1 where su.id=:id")
    void updateClickCount(@Param("id") Long id);

    @EntityGraph(attributePaths = {"createdBy"})
    @Query("select su from ShortUrl su where su.createdBy.id=:Id")
    Page<ShortUrl> findByCreatedBy(@Param("Id") Long id, Pageable pageable);
}