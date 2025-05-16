package com.example.iosfileuploader.core.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    VALIDATION(402),
    SECURITY(403),
    SYSTEM_ERROR(500);

    private final Integer statusCode;
}
