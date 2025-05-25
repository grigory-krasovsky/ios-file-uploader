package com.example.iosfileuploader.tasks;

import com.example.iosfileuploader.core.service.FileDownloadUrlScraper;
import com.example.iosfileuploader.core.service.FileUploader;
import com.example.iosfileuploader.core.service.SharedAlbumService;
import com.example.iosfileuploader.core.service.PlaywrightManager;
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
    PlaywrightManager playwrightManager;

    //todo need to update twice a day
    @Scheduled(fixedDelay = 24L, timeUnit = TimeUnit.HOURS)
    public void updateDynamicUrlParts() {
        sharedAlbumService.findAllEnabled().forEach(playwrightManager::updateDynamicIcloudUrlPart);
    }

    @Scheduled(initialDelay = 4L, fixedDelay = 10L, timeUnit = TimeUnit.MINUTES)
    public void getFreshGuids() {
        sharedAlbumService.findAllEnabled().forEach(a -> {
            System.out.println("start for album " + a.getAlbumId());
            fileDownloadUrlScraper.storeFilesGuids(a);
            System.out.println("finish for album " + a.getAlbumId());
        });
    }

    @Scheduled(fixedDelay = 10L, timeUnit = TimeUnit.MINUTES)
    public void uploadFiles() {
        System.out.println("start uploadFiles");
        fileUploader.uploadForEnabledAlbums();
        System.out.println("finish uploadFiles");
    }
}
