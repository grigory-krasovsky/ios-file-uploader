package com.example.iosfileuploader.core.service;

import java.net.http.HttpRequest;
import java.util.UUID;

public interface HttpRequestService {
    String getResponse(HttpRequest request);

    UUID createNewFileLocation();
}
