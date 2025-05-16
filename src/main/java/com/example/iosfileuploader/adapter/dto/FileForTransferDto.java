package com.example.iosfileuploader.adapter.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileForTransferDto {
    String fileId;
    Set<String> downloadUrls;
}
