package com.example.iosfileuploader.core.exception;

import com.example.iosfileuploader.core.exception.enums.ErrorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class IosUploaderException extends RuntimeException {
    private final ErrorType errorType;
    private final String message;

    protected IosUploaderException(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

}
