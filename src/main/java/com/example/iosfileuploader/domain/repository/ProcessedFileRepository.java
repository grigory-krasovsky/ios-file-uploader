package com.example.iosfileuploader.domain.repository;

import com.example.iosfileuploader.domain.entity.ProcessedFile;
import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessedFileRepository extends JpaRepository<ProcessedFile, UUID> {
    boolean existsByFileIdAndStatusIn(String fileId, List<FileTransferStatus> statuses);

    @Query(value = """
    SELECT pf.*
    FROM processed_file pf
    WHERE pf.status IN (:whitelist)
      AND pf.file_id NOT IN (
        SELECT file_id
        FROM processed_file
        WHERE status IN (:blacklist)
      )
""", nativeQuery = true)
    List<ProcessedFile> getAllByStatusesAndNotByStatuses(
            @Param("whitelist") List<String> whitelist,
            @Param("blacklist") List<String> blacklist
    );
}
