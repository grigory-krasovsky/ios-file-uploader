package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.service.SharedAlbumService;
import com.example.iosfileuploader.core.service.validator.SharedAlbumValidator;
import com.example.iosfileuploader.domain.entity.SharedAlbum;
import com.example.iosfileuploader.domain.repository.SharedAlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharedAlbumServiceImpl extends AbstractEntityService<SharedAlbum, SharedAlbumValidator, SharedAlbumRepository>
        implements SharedAlbumService {
    public SharedAlbumServiceImpl(SharedAlbumValidator validator, SharedAlbumRepository repository) {
        super(validator, repository);
    }

    @Override
    public List<SharedAlbum> findAllEnabled() {
        return repository.findAllByAlbumEnabled(true);
    }
}
