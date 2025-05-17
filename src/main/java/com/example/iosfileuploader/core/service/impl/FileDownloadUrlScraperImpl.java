package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.service.FileDownloadUrlScraper;
import com.example.iosfileuploader.core.utils.http.HttpRequestService;
import com.example.iosfileuploader.core.service.ProcessedFileService;
import com.example.iosfileuploader.domain.entity.ProcessedFile;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileDownloadUrlScraperImpl implements FileDownloadUrlScraper {
    ProcessedFileService processedFileService;
    HttpRequestService httpRequestService;
    static String FILES_URLS_REQUEST_URL =
            "https://p113-sharedstreams.icloud.com/%s/sharedstreams/webasseturls";
    static String ASSETS_GUIDS_REQUEST_URL =
            "https://p113-sharedstreams.icloud.com/%s/sharedstreams/webstream";
    static String ASSET_DOWNLOAD_BASE_URL =  "https://cvws.icloud-content.com%s";
    static String BODY_FOR_GUIDS_REQUST = "{\"streamCtag\":null}";
    static String BODY_FOR_FILES_URLS_REQUST = """
            {
                "photoGuids": ["%s"]
            }
            """;

    @Override
    public Set<Pair<String, String>> getFileNamesAndUrls(SharedAlbum album, String fileId) {
        Set<String> filesUrls = getFilesUrls(album.getAlbumId(), Set.of(fileId));

        Set<Pair<String, String>> result = filesUrls.stream().map(url -> {
            String name = parseFileNameFromTempUrl(url);
            return Pair.of(name, url);
        }).collect(Collectors.toSet());

        processedFileService.create(ProcessedFile.builder()
                .sharedAlbum(album)
                .fileId(fileId)
                .status(FileTransferStatus.TEMP_URLS_ACQUIRED)
                .build());
        return result;
    }

    @Override
    public void storeFilesGuids(SharedAlbum album) {
        Set<String> assetsGuilds = getAssetsGuilds(album.getAlbumId());
        //Todo optimize
        Set<String> freshGuids = assetsGuilds.stream().filter(processedFileService::guidStorageIsPossible).collect(Collectors.toSet());
        List<ProcessedFile> processedFiles = freshGuids.stream().map(guid -> {
            ProcessedFile file = ProcessedFile.builder()
                    .status(FileTransferStatus.FILE_GUID_ACQUIRED)
                    .fileId(guid)
                    .sharedAlbum(album)
                    .build();
            return file;
        }).toList();
        processedFileService.batchSave(processedFiles);
    }

    private Set<String> getAssetsGuilds(String albumId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(ASSETS_GUIDS_REQUEST_URL, albumId)))
                .POST(HttpRequest.BodyPublishers.ofString(BODY_FOR_GUIDS_REQUST))
                .build();

        return parseAssetsGuids(httpRequestService.getResponse(request));
    }

    private Set<String> getFilesUrls(String albumId, Set<String> assetsGuids) {

        String assetsString = String.join("\",\"", assetsGuids);

        String requestBody = String.format(BODY_FOR_FILES_URLS_REQUST, assetsString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(FILES_URLS_REQUEST_URL, albumId)))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        String body = httpRequestService.getResponse(request);

        return parseAssetDownloadUrls(body);
    }

    private Set<String> parseAssetsGuids(String body) {
        JsonNode rootNode = prepareNode(body);
        Set<String> guids = new HashSet<>();

        rootNode.path("photos").forEach(photo -> {
            String photoGuid = photo.path("photoGuid").asText();
            guids.add(photoGuid);
        });
        return guids;
    }

    private static Set<String> parseAssetDownloadUrls(String body) {
        JsonNode rootNode = prepareNode(body);
        Set<String> urls = new HashSet<>();

        rootNode.path("items").forEach(photo -> {
            String photoGuid = photo.path("url_path").asText();

            urls.add(String.format(ASSET_DOWNLOAD_BASE_URL, photoGuid));
        });
        return urls;
    }

    private static JsonNode prepareNode(String body) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rootNode;
    }

    private String parseFileNameFromTempUrl(String tempUrl) {
        Pattern pattern = Pattern.compile(".*/([^/?]+)\\?.*");
        Matcher matcher = pattern.matcher(tempUrl);
        String filename = UUID.randomUUID().toString();
        if (matcher.find()) {
            return matcher.group(1);
        }
        return filename;
    }
}