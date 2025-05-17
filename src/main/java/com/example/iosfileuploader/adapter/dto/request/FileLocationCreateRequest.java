package com.example.iosfileuploader.adapter.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileLocationCreateRequest {

    FileMetadataDto fileMetadataDTO;

    @Builder
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class FileMetadataDto {
        String author;
    }

    public static FileLocationCreateRequest createNew() {
        return FileLocationCreateRequest.builder()
                .fileMetadataDTO(FileLocationCreateRequest.FileMetadataDto.builder()
                        .author("Default")
                        .build())
                .build();
    }
}
