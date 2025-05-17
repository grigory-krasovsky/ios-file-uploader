package com.example.iosfileuploader.core.utils.grpc;

import com.filestorage.grpc.GrpcFileAccessSaveResponse;

public class LoggerResponseHandler implements StreamResponseHandler {
    @Override
    public void onSuccess(GrpcFileAccessSaveResponse response) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onCompleted() {

    }
}
