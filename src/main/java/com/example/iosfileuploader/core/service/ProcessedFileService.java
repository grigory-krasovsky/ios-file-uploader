package com.example.iosfileuploader.core.service;

import com.example.iosfileuploader.domain.entity.ProcessedFile;

import java.util.List;

public interface ProcessedFileService extends CRUDService<ProcessedFile> {
    Boolean transferIsPossible(String fileId);
    List<ProcessedFile> getFreshGuids();
}
