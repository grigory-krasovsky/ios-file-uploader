package com.example.iosfileuploader.core.service.validator;

import com.example.iosfileuploader.domain.entity.SharedAlbum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharedAlbumValidator implements EntityValidator<SharedAlbum> {
    @Override
    public void correctForCreate(SharedAlbum entity) {
        correctCreatedAt(entity);
    }

    @Override
    public void correctForUpdate(SharedAlbum entity) {

    }

    @Override
    public void correctForBatchSave(List<SharedAlbum> entities) {
        entities.forEach(this::correctCreatedAt);
    }
}
