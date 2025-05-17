package com.example.iosfileuploader.core.utils.grpc;

import com.example.iosfileuploader.core.utils.grpc.FileChunkSender;
import com.example.iosfileuploader.core.utils.grpc.StreamResponseHandler;
import com.filestorage.grpc.FileStorageServiceGrpc;
import com.filestorage.grpc.GrpcFileAccessSaveResponse;
import com.filestorage.grpc.GrpcFileChunk;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileStreamer {
    private final FileStorageServiceGrpc.FileStorageServiceStub asyncStub;

    public FileChunkSender startStreamingUpload(
            String fileId,
            String filename,
            String contentType,
            StreamResponseHandler responseHandler) {

        StreamObserver<GrpcFileChunk> requestObserver = asyncStub.saveFileStream(
                new StreamObserver<>() {
                    @Override
                    public void onNext(GrpcFileAccessSaveResponse response) {
                        responseHandler.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable t) {
                        responseHandler.onError(t);
                    }

                    @Override
                    public void onCompleted() {
                        responseHandler.onCompleted();
                    }
                });

        return new FileChunkSender(requestObserver, fileId, filename, contentType);
    }
}