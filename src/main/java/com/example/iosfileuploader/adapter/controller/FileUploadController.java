package com.example.iosfileuploader.adapter.controller;

//import com.example.iosfileuploader.core.service.FileScraper;

import com.example.iosfileuploader.core.service.FileDownloadUrlScraper;
import com.example.iosfileuploader.core.service.FileUploader;
import com.example.iosfileuploader.core.service.SharedAlbumService;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploader")
@AllArgsConstructor
public class FileUploadController {

    private final FileDownloadUrlScraper fileDownloadUrlScraper;
    private final SharedAlbumService sharedAlbumService;
    private final FileUploader fileUploader;

    @GetMapping("/upload")
    public void upload()  {
        fileUploader.uploadForEnabledAlbums();
    }

    @GetMapping("/saveFreshGuids")
    public void saveFreshGuids()  {
        SharedAlbum sharedAlbum = sharedAlbumService.findAllEnabled().stream().findAny().orElseThrow(() -> new RuntimeException());
        fileDownloadUrlScraper.storeFilesGuids(sharedAlbum);
    }
}
