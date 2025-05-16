package com.example.iosfileuploader.domain.repository;

import com.example.iosfileuploader.domain.entity.ProcessedFile;
import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessedFileRepository extends JpaRepository<ProcessedFile, UUID> {
    boolean existsByFileIdAndStatusIn(String fileId, List<FileTransferStatus> statuses);
    List<ProcessedFile> getAllByStatus(FileTransferStatus status);
}
