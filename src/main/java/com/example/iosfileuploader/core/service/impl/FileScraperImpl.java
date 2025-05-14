package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.service.FileScraper;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileScraperImpl implements FileScraper {

    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15";
    private static final String ALBUM_URL =
            "https://www.icloud.com/sharedalbum/ru-ru/#B1p532ODWG65b15";;
    @Override
    public void getFile() {

        try {
            String albumHtml = fetchAlbumPage(ALBUM_URL);
            System.out.println("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String fetchAlbumPage(String url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch album page. HTTP code: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }

    private static List<String> extractPhotoUrls(String html) {
        List<String> urls = new ArrayList<>();

        // This regex is a simplified example - actual implementation would need to analyze the album HTML structure
        Pattern pattern = Pattern.compile("https://[^\"]+?/large/.*?\\.(jpg|jpeg|png|heic)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }
}
