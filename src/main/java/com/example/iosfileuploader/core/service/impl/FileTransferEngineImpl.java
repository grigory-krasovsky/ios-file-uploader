package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.adapter.dto.FileTransferRequest;
import com.example.iosfileuploader.core.service.FileTransferEngine;
import com.example.iosfileuploader.core.utils.SystemParameterManager;
import com.example.iosfileuploader.core.utils.grpc.FileChunkSender;
import com.example.iosfileuploader.core.utils.grpc.FileStreamer;
import com.example.iosfileuploader.core.utils.grpc.LoggerResponseHandler;
import com.example.iosfileuploader.core.utils.grpc.StreamResponseHandler;
import com.filestorage.grpc.FileStorageServiceGrpc;
import com.filestorage.grpc.GrpcFileAccessSaveRequest;
import com.filestorage.grpc.GrpcFileAccessSaveResponse;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileTransferEngineImpl implements FileTransferEngine {

    final SystemParameterManager systemParameterManager;
    final ManagedChannel channel;
    @GrpcClient("file-storage")
    FileStorageServiceGrpc.FileStorageServiceBlockingStub blockingStub;
    @GrpcClient("file-storage")
    FileStorageServiceGrpc.FileStorageServiceStub asyncStub;

    public FileTransferEngineImpl(ManagedChannel channel, SystemParameterManager systemParameterManager) {
        this.systemParameterManager = systemParameterManager;
        this.channel = channel;
    }

    @Override
    public void transferFile(FileTransferRequest request) {

        FileStorageServiceGrpc.FileStorageServiceBlockingStub blockingStub = FileStorageServiceGrpc.newBlockingStub(channel);

        GrpcFileAccessSaveResponse response = blockingStub.saveFile(GrpcFileAccessSaveRequest.newBuilder()
                .setId(request.getId())
                .setContents(ByteString.copyFrom(request.getData()))
                .setFilename(request.getFileName())
                .setContentType(request.getContentType())
                .build());
    }

    @Override
    public void transferFileStreaming(FileTransferRequest request) {

        StreamResponseHandler handler = new LoggerResponseHandler();
        Integer chunkSize = systemParameterManager.getParam("chunkSize", Integer.class);
        // Start streaming
        FileChunkSender sender = new FileStreamer(asyncStub).startStreamingUpload(
                request.getId(),
                request.getFileName(),
                request.getContentType(),
                handler);
        try {
            // Split and send chunks
            byte[] data = request.getData();
            int totalChunks = (int) Math.ceil((double) data.length / chunkSize);

            for (int i = 0; i < totalChunks; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, data.length);
                byte[] chunk = Arrays.copyOfRange(data, start, end);
                sender.sendChunk(chunk, i + 1);
            }

            sender.complete();
        } catch (Exception e) {
            sender.cancel(e);
        }
    }
}