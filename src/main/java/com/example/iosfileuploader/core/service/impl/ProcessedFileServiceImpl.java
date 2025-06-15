package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.service.ProcessedFileService;
import com.example.iosfileuploader.core.service.validator.ProcessedFileValidator;
import com.example.iosfileuploader.domain.entity.ProcessedFile;
import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import com.example.iosfileuploader.domain.repository.ProcessedFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessedFileServiceImpl extends AbstractEntityService<ProcessedFile, ProcessedFileValidator, ProcessedFileRepository> implements ProcessedFileService {
    public ProcessedFileServiceImpl(ProcessedFileValidator validator, ProcessedFileRepository repository) {
        super(validator, repository);
    }

    @Override
    public Boolean transferIsPossible(String fileId) {
        return !repository.existsByFileIdAndStatusIn(fileId, List.of(FileTransferStatus.TRANSFER_SUCCESS, FileTransferStatus.TRANSFER_IN_PROGRESS));
    }

    @Override
    public Boolean guidStorageIsPossible(String fileId) {
        return !repository.existsByFileIdAndStatusIn(fileId, List.of(FileTransferStatus.FILE_GUID_ACQUIRED));
    }

    @Override
    public List<ProcessedFile> getFreshGuids() {
        return repository.getAllByStatusesAndNotByStatuses(List.of(FileTransferStatus.FILE_GUID_ACQUIRED.name()),
                List.of(FileTransferStatus.FILE_BATCH_TRANSFER_SUCCESS.name(), FileTransferStatus.TRANSFER_IN_PROGRESS.name()));
    }
}
