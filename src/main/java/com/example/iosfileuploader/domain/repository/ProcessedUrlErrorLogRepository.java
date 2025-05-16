package com.example.iosfileuploader.domain.repository;

import com.example.iosfileuploader.domain.entity.ProcessedUrlErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedUrlErrorLogRepository extends JpaRepository<ProcessedUrlErrorLog, UUID> {
}
