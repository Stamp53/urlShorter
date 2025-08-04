package com.tinyurl.demo;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UrlRequestDto {
    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^(http|https)://.+", message = "Invalid URL format")
    private String originalUrl;

    private Integer expiryDays;

    // Constructors
    public UrlRequestDto() {}

    public UrlRequestDto(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    // Getters and Setters
    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }
}