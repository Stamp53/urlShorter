package com.tinyurl.demo;


public interface UrlService {
    UrlResponseDto shortenUrl(UrlRequestDto requestDto);
    Url getOriginalUrl(String shortCode);
    void incrementClickCount(Url url);
    void deleteUrl(String shortCode);
}
