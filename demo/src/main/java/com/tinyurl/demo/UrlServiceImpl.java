package com.tinyurl.demo;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();

    @Override
    public UrlResponseDto shortenUrl(UrlRequestDto requestDto) {
        // Validate URL
        if (!isValidUrl(requestDto.getOriginalUrl())) {
            throw new InvalidUrlException("Invalid URL: " + requestDto.getOriginalUrl());
        }

        // Generate unique short code
        String shortCode = generateUniqueShortCode();

        // Create URL entity
        Url url = new Url(requestDto.getOriginalUrl(), shortCode);

        // Set expiration if provided
        if (requestDto.getExpiryDays() != null && requestDto.getExpiryDays() > 0) {
            url.setExpiresAt(LocalDateTime.now().plusDays(requestDto.getExpiryDays()));
        }

        // Save to database
        url = urlRepository.save(url);

        // Create response
        return new UrlResponseDto(
                baseUrl + "/" + shortCode,
                url.getOriginalUrl(),
                url.getCreatedAt(),
                url.getExpiresAt(),
                url.getClickCount()
        );
    }

    @Override
    public Url getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for code: " + shortCode));

        // Check if URL is active
        if (!url.getIsActive()) {
            throw new UrlNotFoundException("URL is no longer active");
        }

        // Check if URL has expired
        if (url.getExpiresAt() != null && LocalDateTime.now().isAfter(url.getExpiresAt())) {
            throw new UrlNotFoundException("URL has expired");
        }

        return url;
    }

    @Override
    public void incrementClickCount(Url url) {
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
    }

    @Override
    public void deleteUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for code: " + shortCode));
        url.setIsActive(false);
        urlRepository.save(url);
    }

    private String generateUniqueShortCode() {
        String shortCode;
        int attempts = 0;
        do {
            shortCode = generateRandomString(SHORT_CODE_LENGTH);
            attempts++;

            // Prevent infinite loop
            if (attempts > 10) {
                shortCode = generateRandomString(SHORT_CODE_LENGTH + (attempts / 10));
            }
        } while (urlRepository.findByShortCode(shortCode).isPresent());

        return shortCode;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private boolean isValidUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
