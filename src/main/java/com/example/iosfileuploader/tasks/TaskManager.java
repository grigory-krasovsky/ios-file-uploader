package com.example.iosfileuploader.tasks;

import com.example.iosfileuploader.core.service.FileDownloadUrlScraper;
import com.example.iosfileuploader.core.service.FileUploader;
import com.example.iosfileuploader.core.service.SharedAlbumService;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskManager {
    FileUploader fileUploader;
    SharedAlbumService sharedAlbumService;
    FileDownloadUrlScraper fileDownloadUrlScraper;
    @Scheduled(fixedDelay = 10L, timeUnit = TimeUnit.MINUTES)
    public void getFreshGuids() {
        System.out.println("start");
        SharedAlbum sharedAlbum = sharedAlbumService.findAllEnabled().stream().findAny().orElseThrow(RuntimeException::new);
        fileDownloadUrlScraper.storeFilesGuids(sharedAlbum);
        System.out.println("finish");
    }

    @Scheduled(initialDelay = 2L, fixedDelay = 10L, timeUnit = TimeUnit.MINUTES)
    public void uploadFiles() {
        System.out.println("start");
        fileUploader.uploadForEnabledAlbums();
        System.out.println("finish");
    }
}
