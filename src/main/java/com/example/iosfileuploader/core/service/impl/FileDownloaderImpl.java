package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.service.FileDownloader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class FileDownloaderImpl implements FileDownloader {
    @Override
    public byte[] downloadFile(String url) {
        return getFileBytesFromUrl(url);
    }

    private byte[] getFileBytesFromUrl(String fileUrl) {
        try (InputStream fileStream = new URL(fileUrl).openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];  // 4KB buffer
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
