package com.urlshortner.urlshortner.service;

import com.urlshortner.urlshortner.repository.ShortUrlRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClickCountService {

    ShortUrlRepository shortUrlRepository;

    public ClickCountService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Async
    @Transactional
    public void updateClickCount(Long id) {
        shortUrlRepository.updateClickCount(id);
    }
}