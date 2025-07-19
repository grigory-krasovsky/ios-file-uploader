package com.example.iosfileuploader.core.utils.http;

import java.net.http.HttpRequest;
import java.util.UUID;

public interface HttpRequestService {
    String getResponse(HttpRequest request);
    UUID createNewFileLocation(UUID mainFileLocationUuid);

    void checkFileSize(String url);
}
