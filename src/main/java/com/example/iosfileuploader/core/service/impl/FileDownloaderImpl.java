package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.service.FileDownloader;
import com.example.iosfileuploader.core.utils.SystemParameterManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileDownloaderImpl implements FileDownloader {
    SystemParameterManager systemParameterManager;
    @Override
    public byte[] downloadFile(String url) {
        return getFileBytesFromUrl(url);
    }

    private byte[] getFileBytesFromUrl(String fileUrl) {
        try (InputStream fileStream = new URL(fileUrl).openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[systemParameterManager.getParam("bufferSize", Integer.class)];
            int bytesRead;

            while ((bytesRead = fileStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
