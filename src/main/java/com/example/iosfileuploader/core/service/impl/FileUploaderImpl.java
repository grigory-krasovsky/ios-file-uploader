package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.adapter.dto.FileTransferRequest;
import com.example.iosfileuploader.core.service.*;
import com.example.iosfileuploader.domain.entity.ProcessedFile;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileUploaderImpl implements FileUploader {

    SharedAlbumService sharedAlbumService;
    FileDownloader fileDownloader;
    FileDownloadUrlScraper fileDownloadUrlScraper;
    ProcessedFileService processedFileService;
    FileTransferEngine fileTransferEngine;
    HttpRequestService httpRequestService;

    public void uploadForEnabledAlbums() {

        Tika tika = new Tika();
        List<SharedAlbum> enabledAlbums = sharedAlbumService.findAllEnabled();
        List<ProcessedFile> filesReadyForDownload = processedFileService.getFreshGuids().stream()
                .limit(1) //debug purpose
                .toList();

        enabledAlbums.forEach(album -> {
            filesReadyForDownload.forEach(file -> {
                Set<Pair<String, String>> namesUrls = fileDownloadUrlScraper.getFileNamesAndUrls(album, file.getFileId());

                List<Pair<String, byte[]>> fileBatch = new ArrayList<>();
                namesUrls.forEach(nameUrl -> {
                    fileBatch.add(Pair.of(nameUrl.getFirst(), fileDownloader.downloadFile(nameUrl.getSecond())));
                });
                processedFileService.create(ProcessedFile.builder()
                                .status(FileTransferStatus.FILE_BATCH_DOWNLOAD_SUCCESS)
                                .sharedAlbum(album)
                                .fileId(file.getFileId())
                        .build());

                fileBatch.forEach(asset -> {
                    UUID newFileLocationUuid = httpRequestService.createNewFileLocation();
                    String contentType;
                    String fileName;
                    contentType = tika.detect(asset.getSecond());
                    fileName = asset.getFirst();

                    FileTransferRequest fileToTransfer = FileTransferRequest.builder()
                            .id(newFileLocationUuid.toString())
                            .contentType(contentType)
                            .fileName(fileName)
                            .data(asset.getSecond())
                            .build();
                    fileTransferEngine.transferFile(fileToTransfer);
                });
                processedFileService.create(ProcessedFile.builder()
                        .status(FileTransferStatus.FILE_BATCH_TRANSFER_SUCCESS)
                        .sharedAlbum(album)
                        .fileId(file.getFileId())
                        .build());
            });
        });
    }
}
