package com.example.iosfileuploader.core.service;

import com.example.iosfileuploader.adapter.dto.FileTransferRequest;

public interface FileTransferEngine {

    void transferFile(FileTransferRequest request);
    void transferFileStreaming(FileTransferRequest request);
}
