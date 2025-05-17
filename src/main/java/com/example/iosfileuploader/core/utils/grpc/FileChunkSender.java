package com.example.iosfileuploader.core.utils.grpc;

import com.filestorage.grpc.GrpcFileChunk;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

public class FileChunkSender {
    private final StreamObserver<GrpcFileChunk> requestObserver;
    private final String fileId;
    private final String filename;
    private final String contentType;

    public FileChunkSender(StreamObserver<GrpcFileChunk> requestObserver,
                           String fileId, String filename, String contentType) {
        this.requestObserver = requestObserver;
        this.fileId = fileId;
        this.filename = filename;
        this.contentType = contentType;
    }

    public void sendChunk(byte[] data, int chunkNumber) {
        requestObserver.onNext(GrpcFileChunk.newBuilder()
                .setId(fileId)
                .setChunk(ByteString.copyFrom(data))
                .setChunkNumber(chunkNumber)
                .setFilename(filename)
                .setContentType(contentType)
                .build());
    }

    public void complete() {
        requestObserver.onCompleted();
    }

    public void cancel(Throwable t) {
        requestObserver.onError(t);
    }
}