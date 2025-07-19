package com.example.iosfileuploader.adapter.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileLocationCreateRequest {

    FileMetadataDto fileMetadataDTO;
    UUID mainFileLocationUuid;

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
