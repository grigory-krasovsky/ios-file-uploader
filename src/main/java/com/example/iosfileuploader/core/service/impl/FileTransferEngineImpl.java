package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.adapter.dto.FileTransferRequest;
import com.example.iosfileuploader.core.service.FileTransferEngine;
import com.filestorage.grpc.FileStorageServiceGrpc;
import com.filestorage.grpc.GrpcFileAccessSaveRequest;
import com.filestorage.grpc.GrpcFileAccessSaveResponse;
import com.google.protobuf.ByteString;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
public class FileTransferEngineImpl implements FileTransferEngine {

    @GrpcClient("file-storage")
    private FileStorageServiceGrpc.FileStorageServiceBlockingStub blockingStub;

    public String uploadFile() throws IOException {


        byte[] fileBytesFromUrl = getFileBytesFromUrl("https://cvws.icloud-content.com/S/AemeoCQ6b3e2ZnlMsqoiip_zlHkN/camphoto_959030623.jpg?o=Asd7SQGN-S0trtvVRsYaZVP-lenseEgo1Dx1E-VEHt4g&v=1&z=https%3A%2F%2Fp113-content.icloud.com%3A443&x=1&a=CAogMGE2jvxf40ZtkORtNVkf1dUEE90Y92xEwMy2HVBLwtESZRDd__Ty7DIY3ZaI-OwyIgEAUgTzlHkNaiWwxmcgBfs75tMGfUnjXqWLcwiqC7HeK-nDnYNze3t4ctw5sPB9ciXENhu_r8S4j0CulQa3l8eQzGsb_eQZJ7oxvu6a6SmQXgdZV-UD&e=1747229739&r=d868f3b9-3eb9-4d92-99b5-099352c8f655-4&s=0-foXxfB2oKlH-jbcu05sswgH-c");

        GrpcFileAccessSaveRequest request = GrpcFileAccessSaveRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setContents(ByteString.copyFrom(fileBytesFromUrl))
                .setFilename("filename.jpg")
                .build();

        GrpcFileAccessSaveResponse response = blockingStub.saveFile(request);
        return response.getFileId();
    }

    private byte[] getFileBytesFromUrl(String photoUrl) throws IOException {
        try (InputStream fileStream = new URL(photoUrl).openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];  // 4KB buffer
            int bytesRead;

            while ((bytesRead = fileStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        }
    }

    @Override
    public void transferFile(FileTransferRequest dto) {
        GrpcFileAccessSaveRequest request = GrpcFileAccessSaveRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setContents(ByteString.copyFrom(dto.getData()))
                .setFilename(dto.getFileName())
                .setContentType(dto.getContentType())
                .build();

        GrpcFileAccessSaveResponse response = blockingStub.saveFile(request);
    }
}
