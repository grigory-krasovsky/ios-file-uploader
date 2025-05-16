package com.example.iosfileuploader.core.service;

import com.example.iosfileuploader.domain.entity.SharedAlbum;
import org.springframework.data.util.Pair;

import java.util.Set;

public interface FileDownloadUrlScraper {
//    Set<String> getFileDownloadUrls(SharedAlbum album);
    Set<Pair<String, String>> getFileNamesAndUrls(SharedAlbum album, String fileId);
    void storeFilesGuids(SharedAlbum album);
}
