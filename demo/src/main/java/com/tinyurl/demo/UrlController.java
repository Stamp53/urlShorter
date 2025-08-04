package com.tinyurl.demo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin(origins = "*")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDto> shortenUrl(@Valid @RequestBody UrlRequestDto requestDto) {
        UrlResponseDto response = urlService.shortenUrl(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponseDto> getUrlInfo(@PathVariable String shortCode) {
        var url = urlService.getOriginalUrl(shortCode);
        UrlResponseDto response = new UrlResponseDto(
                "", // Short URL not needed in this context
                url.getOriginalUrl(),
                url.getCreatedAt(),
                url.getExpiresAt(),
                url.getClickCount()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}
