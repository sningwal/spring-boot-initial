package com.urlshortner.urlshortner.dtos.response;

public interface UrlRedirectView {
    String getOriginalUrl();
    Boolean getIsPrivate();
    Long getId();
}