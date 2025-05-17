package com.example.iosfileuploader.core.utils.http;

import com.example.iosfileuploader.adapter.dto.request.FileLocationCreateRequest;
import com.example.iosfileuploader.adapter.dto.response.FileLocationCreateResponse;
import com.example.iosfileuploader.core.utils.SystemParameterManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HttpRequestServiceImpl implements HttpRequestService {
    SystemParameterManager systemParameterManager;
    @Override
    public String getResponse(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    @Override
    public UUID createNewFileLocation() {
        String baseUrl = systemParameterManager.getParam("fileStorageBaseUrl", String.class);
        System.out.println("baseUrl " + baseUrl);

        FileLocationCreateRequest body = FileLocationCreateRequest.createNew();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/file/location"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        FileLocationCreateResponse response;
        try {
            response = objectMapper.readValue(
                    getResponse(request),
                    FileLocationCreateResponse.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response.getFileLocationUUID();
    }

    @Override
    public void checkFileSize(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        getResponse(request);
    }
}
