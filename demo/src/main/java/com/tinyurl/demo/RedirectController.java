package com.tinyurl.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {

    @Autowired
    private UrlService urlService;

    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        Url url = urlService.getOriginalUrl(shortCode);
        urlService.incrementClickCount(url);
        return new RedirectView(url.getOriginalUrl());
    }
}
