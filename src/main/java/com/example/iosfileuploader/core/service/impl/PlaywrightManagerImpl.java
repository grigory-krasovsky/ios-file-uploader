package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.service.PlaywrightManager;
import com.example.iosfileuploader.core.service.SharedAlbumService;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import com.microsoft.playwright.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PlaywrightManagerImpl implements PlaywrightManager {
    SharedAlbumService sharedAlbumService;

    public void updateDynamicIcloudUrlPart (SharedAlbum album) {
        System.out.println("start playwright");
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Listen for network responses
            page.onResponse(response -> {
                String url = response.url();
                if (url.contains("sharedstreams/webstream")) {
                    String substring = url.substring(0, url.lastIndexOf('/'));
                    album.setBaseUrl(substring);
                    sharedAlbumService.update(album);
                    System.out.println("Album base url: " + substring);
                }
            });

            // Load the iCloud shared album page
            String sharedAlbumUrl = String.format("https://www.icloud.com/sharedalbum/ru-ru/#%s", album.getAlbumId());
            page.navigate(sharedAlbumUrl);

            // Wait long enough for all JS requests to load
            page.waitForTimeout(60000); // or use waitUntil="networkidle" if preferred

            browser.close();
        }
        System.out.println("end playwright");
    }
}