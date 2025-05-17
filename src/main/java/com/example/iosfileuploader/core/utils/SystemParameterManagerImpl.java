package com.example.iosfileuploader.core.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class SystemParameterManagerImpl implements SystemParameterManager {
    private final ConversionService conversionService;
    private final Map<String, Serializable> parameters;

    public SystemParameterManagerImpl(
            ConversionService conversionService,
            @Value("${file-storage-base-url}") String fileStorageBaseUrl,
            @Value("${file-max-size}") Long fileMaxSize,
            @Value("${grpc-server.domain}") String grpcServerDomain,
            @Value("${buffer-size}") Integer bufferSize,
            @Value("${chunk-size}") Integer chunkSize,
            @Value("${grpc-server.port}") Long grpcServerPort
    ) {
        this.conversionService = conversionService;
        this.parameters = Map.of(
                "fileStorageBaseUrl", fileStorageBaseUrl,
                "fileMaxSize", fileMaxSize,
                "chunkSize", chunkSize,
                "grpcServerDomain", grpcServerDomain,
                "bufferSize", bufferSize,
                "grpcServerPort", grpcServerPort
        );
    }

    @Override
    public <T> T  getParam(String paramName, Class<T> type) {
        Serializable value = parameters.get(paramName);
        if (value == null) {
            throw new IllegalArgumentException("Parameter '" + paramName + "' not found");
        }
        return convertValue(value, type);
    }

    private <T> T convertValue(Object value, Class<T> targetType) {
        if (targetType.isInstance(value)) {
            return targetType.cast(value);
        }
        if (conversionService.canConvert(value.getClass(), targetType)) {
            return conversionService.convert(value, targetType);
        }
        throw new IllegalArgumentException(
                "Cannot convert value '" + value + "' of type " + value.getClass() +
                        " to target type " + targetType
        );
    }
}
