package com.example.iosfileuploader.core.utils.grpc;

import com.filestorage.grpc.GrpcFileChunk;
import io.grpc.stub.StreamObserver;

public abstract class AbstractFileUploadStreamObserver implements StreamObserver<GrpcFileChunk> {
    @Override
    public void onNext(GrpcFileChunk grpcFileChunk) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
