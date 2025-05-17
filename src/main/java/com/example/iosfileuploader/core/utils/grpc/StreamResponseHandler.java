package com.example.iosfileuploader.core.utils.grpc;

import com.filestorage.grpc.GrpcFileAccessSaveResponse;

public interface StreamResponseHandler {
    void onSuccess(GrpcFileAccessSaveResponse response);
    void onError(Throwable t);
    void onCompleted();
}