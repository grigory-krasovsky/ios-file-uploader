package com.example.iosfileuploader.core.service.validator;

import com.example.iosfileuploader.domain.entity.ProcessedFile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessedFileValidator implements EntityValidator<ProcessedFile> {
    @Override
    public void correctForCreate(ProcessedFile entity) {
        correctCreatedAt(entity);
    }
    @Override
    public void correctForUpdate(ProcessedFile entity) {
    }
    @Override
    public void correctForBatchSave(List<ProcessedFile> entities) {
        entities.forEach(this::correctCreatedAt);
    }
}
