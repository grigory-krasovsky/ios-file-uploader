package com.example.iosfileuploader.adapter.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileTransferRequest {

    String id;
    String contentType;
    String fileName;
    byte[] data;
}
