package com.example.iosfileuploader.domain.repository;

import com.example.iosfileuploader.domain.entity.SharedAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SharedAlbumRepository extends JpaRepository<SharedAlbum, UUID> {
    List<SharedAlbum> findAllByAlbumEnabled(Boolean enabled);
}
