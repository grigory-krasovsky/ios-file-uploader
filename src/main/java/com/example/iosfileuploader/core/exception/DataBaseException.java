package com.example.iosfileuploader.core.exception;

import com.example.iosfileuploader.core.exception.enums.ErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataBaseException extends IosUploaderException {

    public DataBaseException(ErrorType errorType, String message) {
        super(errorType, message);
    }

    public static String ENTITY_IS_ABSENT_MESSAGE(UUID id, Class<?> clazz) {
        return String.format("Service: %s. Unable to find entity with id %s", clazz.getName(), id);
    }
}
