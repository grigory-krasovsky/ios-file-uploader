package com.example.iosfileuploader.domain.entity;

import com.example.iosfileuploader.domain.enums.FileTransferStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "processed_file")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessedFile extends AbstractEntity {
    @ManyToOne
            @JoinColumn(name = "album_id")
    SharedAlbum sharedAlbum;
    String fileId;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT")
    FileTransferStatus status;
}
