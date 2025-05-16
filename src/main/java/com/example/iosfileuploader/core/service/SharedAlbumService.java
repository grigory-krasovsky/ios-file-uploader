package com.example.iosfileuploader.core.service;

import com.example.iosfileuploader.domain.entity.SharedAlbum;

import java.util.List;

public interface SharedAlbumService extends CRUDService<SharedAlbum> {
    List<SharedAlbum> findAllEnabled();
}
