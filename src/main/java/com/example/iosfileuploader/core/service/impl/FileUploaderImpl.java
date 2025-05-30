package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.adapter.dto.FileTransferRequest;
import com.example.iosfileuploader.core.service.*;
import com.example.iosfileuploader.core.utils.SystemParameterManager;
import com.example.iosfileuploader.core.utils.http.HttpRequestService;
import com.example.iosfileuploader.domain.entity.ProcessedFile;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.tika.Tika;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private final AtomicInteger totalFilesDownloaded = new AtomicInteger(0);
    public void uploadForEnabledAlbums() {

        System.out.println("Start: " + LocalDateTime.now());

        Map<SharedAlbum, List<ProcessedFile>> albumFreshGuids = processedFileService.getFreshGuids().stream()
                .collect(Collectors.groupingBy(ProcessedFile::getSharedAlbum));

        ExecutorService executor = Executors.newFixedThreadPool(32); // even 16 could be enough

        albumFreshGuids.forEach((album, guids) -> {
            if (album.getAlbumEnabled()) {
                List<CompletableFuture<Void>> fileFutures = guids.stream()
                        .map(file -> CompletableFuture.runAsync(() -> processFileBatch(album, file), executor))
                        .toList();

                CompletableFuture.allOf(fileFutures.toArray(new CompletableFuture[0])).join();
            }
        });

        executor.shutdown();
        System.out.println(totalFilesDownloaded.get());
        System.out.println("Finish: " + LocalDateTime.now());
    }

    public void processFileBatch(SharedAlbum album, ProcessedFile file) {
        Long maxSize = systemParameterManager.getParam("fileMaxSize", Long.class);

        Tika tika = new Tika();

        Set<Pair<String, String>> namesUrls = fileDownloadUrlScraper.getFileNamesAndUrls(album, file.getFileId());

        List<Pair<String, byte[]>> fileBatch = namesUrls.parallelStream()
                .map(nameUrl -> {
                    byte[] data = fileDownloader.downloadFile(nameUrl.getSecond());
                    return Pair.of(nameUrl.getFirst(), data);
                })
                .toList();
        totalFilesDownloaded.addAndGet(namesUrls.size());

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

        String contentType = tika.detect(asset.getSecond());
        String fileName = asset.getFirst();

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
        processedFileService.create(ProcessedFile.builder()
                .status(FileTransferStatus.FILE_BATCH_TRANSFER_SUCCESS)
                .sharedAlbum(album)
                .fileId(file.getFileId())
                .build());
    }
}
