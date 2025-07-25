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
import java.util.concurrent.atomic.AtomicReference;
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
    private final AtomicInteger totalFilesInAlbum = new AtomicInteger(0);
    private final AtomicInteger totalFilesTransferred = new AtomicInteger(0);

    public void uploadForEnabledAlbums() {

        System.out.println("Start: " + LocalDateTime.now());

        Map<SharedAlbum, List<ProcessedFile>> albumFreshGuids = processedFileService.getFreshGuids().stream()
                .collect(Collectors.groupingBy(ProcessedFile::getSharedAlbum));

        ExecutorService executor = Executors.newFixedThreadPool(32); // even 16 could be enough

        albumFreshGuids.forEach((album, guids) -> {
            System.out.printf("Album %s has %s fresh guids%n", album.getAlbumId(), guids.size());
        });

        albumFreshGuids.forEach((album, guids) -> {
            if (album.getAlbumEnabled()) {
                List<CompletableFuture<Void>> fileFutures = guids.stream()
                        .map(file -> CompletableFuture.runAsync(() -> processFileBatch(album, file), executor))
                        .toList();

                CompletableFuture.allOf(fileFutures.toArray(new CompletableFuture[0])).join();
            }
        });

        executor.shutdown();
        System.out.println("Files in album " + totalFilesInAlbum.get());
        System.out.println("Finish: " + LocalDateTime.now());
    }

    public void processFileBatch(SharedAlbum album, ProcessedFile file) {
//        if (!file.getFileId().equals("34E805E4-1AF0-4005-84DF-8C491B28FC61")) return;
        Long maxSize = systemParameterManager.getParam("fileMaxSize", Long.class);

        Tika tika = new Tika();

        Set<Pair<String, String>> namesUrls = fileDownloadUrlScraper.getFileNamesAndUrls(album, file.getFileId());

        List<Pair<String, byte[]>> fileBatch = namesUrls.parallelStream()
                .map(nameUrl -> {
                    byte[] data = fileDownloader.downloadFile(nameUrl.getSecond());
                    return Pair.of(nameUrl.getFirst(), data);
                })
                .toList();
        totalFilesInAlbum.addAndGet(namesUrls.size());

        processedFileService.create(ProcessedFile.builder()
                .status(FileTransferStatus.FILE_BATCH_DOWNLOAD_SUCCESS)
                .sharedAlbum(album)
                .fileId(file.getFileId())
                .build());

        final AtomicReference<UUID> mainFileLocationUuid = new AtomicReference<>(null);
        fileBatch.stream()
                .sorted(Comparator.<Pair<String, byte[]>>comparingLong(
                        f -> f.getSecond().length
                ).reversed())
                .forEach(singleFile -> {
                    UUID uuid = transferSingleFile(singleFile, mainFileLocationUuid.get());
                    mainFileLocationUuid.compareAndSet(null, uuid);
                });

        processedFileService.create(ProcessedFile.builder()
                .status(FileTransferStatus.FILE_BATCH_TRANSFER_SUCCESS)
                .sharedAlbum(album)
                .fileId(file.getFileId())
                .build());
    }

    private UUID transferSingleFile(Pair<String, byte[]> asset, UUID mainFileLocationUuid) {
        UUID newFileLocationUuid = httpRequestService.createNewFileLocation(mainFileLocationUuid);

        String contentType = new Tika().detect(asset.getSecond());
        String fileName = asset.getFirst();

        FileTransferRequest fileToTransfer = FileTransferRequest.builder()
                .id(newFileLocationUuid.toString())
                .contentType(contentType)
                .fileName(fileName)
                .data(asset.getSecond())
                .build();
        fileTransferEngine.transferFile(fileToTransfer);
        totalFilesTransferred.addAndGet(1);
        return newFileLocationUuid;
    }
}
