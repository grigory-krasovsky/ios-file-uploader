package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.adapter.dto.FileTransferRequest;
import com.example.iosfileuploader.core.service.*;
import com.example.iosfileuploader.core.utils.SystemParameterManager;
import com.example.iosfileuploader.core.utils.http.HttpRequestService;
import com.example.iosfileuploader.domain.entity.ProcessedFile;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileUploaderImpl implements FileUploader {
    SharedAlbumService sharedAlbumService;
    FileDownloader fileDownloader;
    FileDownloadUrlScraper fileDownloadUrlScraper;
    ProcessedFileService processedFileService;
    FileTransferEngine fileTransferEngine;
    HttpRequestService httpRequestService;
    SystemParameterManager systemParameterManager;

    public void uploadForEnabledAlbums() {

        Long maxSize = systemParameterManager.getParam("fileMaxSize", Long.class);

        Tika tika = new Tika();
        List<SharedAlbum> enabledAlbums = sharedAlbumService.findAllEnabled();
        List<ProcessedFile> filesReadyForDownload = processedFileService.getFreshGuids().stream()
//                .limit(1)
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

                //we do not need previews
                Pair<String, byte[]> asset = fileBatch.stream()
                        .max(Comparator.comparing(pair -> pair.getSecond().length))
                        .orElseThrow(() -> new RuntimeException("Batch is empty"));

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

                if (fileToTransfer.getData().length > maxSize) {
                    fileTransferEngine.transferFileStreaming(fileToTransfer);
                } else {
                    fileTransferEngine.transferFile(fileToTransfer);
                }
                ;
                processedFileService.create(ProcessedFile.builder()
                        .status(FileTransferStatus.FILE_BATCH_TRANSFER_SUCCESS)
                        .sharedAlbum(album)
                        .fileId(file.getFileId())
                        .build());
            });
        });
        System.out.println("");
    }
}
